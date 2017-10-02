package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.ImageManService;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.model.LiveDataAndStatus;
import org.ditto.lib.repository.model.ImageRequest;
import org.ditto.lib.repository.model.Status;
import org.ditto.sexyimage.grpc.Common;
import org.ditto.sexyimage.grpc.Imageman;

import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository that handles IndexImage objects.
 */
@Singleton
public class IndexImageRepository {
    private final static String TAG = IndexImageRepository.class.getSimpleName();

    private final static Gson gson = new Gson();
    private ApigrpcFascade apigrpcFascade;
    private RoomFascade roomFascade;

    @Inject
    public IndexImageRepository(ApigrpcFascade apigrpcFascade,
                                RoomFascade roomFascade) {
        this.roomFascade = roomFascade;
        this.apigrpcFascade = apigrpcFascade;
    }


    public LiveDataAndStatus<PagedList<IndexImage>> list2ImagesBy(ImageRequest imageRequest) {
        LiveData<Status> liveStatus = new LiveData<Status>() {
            @Override
            protected void onActive() {
                //will refresh when load the first page
                if (imageRequest.refresh) {
                    postValue(Status.builder().setCode(Status.Code.START).setRefresh(true).build());
                    apigrpcFascade.getImageManService().listImages(imageRequest.imageType, 0,
                            new ImageManService.ImageManCallback() {
                                @Override
                                public void onApiReady() {
                                    postValue(Status.builder().setCode(Status.Code.LOADING).setRefresh(true).build());
                                }

                                @Override
                                public void onImageReceived(Common.ImageResponse response) {
                                    postValue(Status.builder().setCode(Status.Code.LOADING).setRefresh(true).build());

                                    Log.i(TAG, String.format("onImageReceived save to database, image.url=%s\n image=[%s]", response.getUrl(), gson.toJson(response)));
                                    IndexImage indexImage = IndexImage.builder()
                                            .setUrl(response.getUrl())
                                            .setInfoUrl(response.getInfoUrl())
                                            .setTitle(response.getTitle())
                                            .setDesc(response.getDesc())
                                            .setType(response.getType().name())
                                            .setLastUpdated(response.getLastUpdated())
                                            .build();
                                    roomFascade.daoIndexImage.save(indexImage);
                                }

                                @Override
                                public void onImageDeleted(Common.StatusResponse value) {
                                    postValue(Status.builder().setCode(Status.Code.LOADING).setRefresh(true).build());
                                }


                                @Override
                                public void onImageUpserted(Common.StatusResponse statusResponse) {
                                    postValue(Status.builder().setCode(Status.Code.LOADING).setRefresh(true).build());
                                }

                                @Override
                                public void onApiCompleted() {
                                    postValue(Status.builder().setCode(Status.Code.END_SUCCESS).setRefresh(true).build());
                                }

                                @Override
                                public void onApiError() {
                                    postValue(Status.builder().setCode(Status.Code.END_DISCONNECTED).setRefresh(true).setMessage("aaaaaa").build());
                                }
                            });
                } else {
                    postValue(Status.builder().setCode(Status.Code.LOADING).setLoadMore(true).build());
                }

            }

            @Override
            protected void onInactive() {
                super.onInactive();
            }
        };
        // TODO: trying paging library
        LiveData<PagedList<IndexImage>> liveData = roomFascade.daoIndexImage.listPagingImageIndicesBy(imageRequest.imageType.name())
                .create(imageRequest.page * imageRequest.pageSize,
                        new PagedList.Config
                                .Builder()
                                .setPageSize(imageRequest.pageSize)
                                .setPrefetchDistance(imageRequest.pageSize)
                                .setEnablePlaceholders(true)
                                .build());
//        LiveData<List<IndexImage>> liveData = roomFascade.daoIndexImage.listImageIndicesBy(imageType.name(), page, pageSize);
        return new LiveDataAndStatus<>(liveData, liveStatus);
    }


    public LiveData<IndexImage> find(String imageUrl) {
        return roomFascade.daoIndexImage.findLive(imageUrl);
    }


    public LiveData<Status> delete(String url) {
        return new LiveData<Status>() {
            @Override
            protected void onActive() {
                Log.i(TAG,String.format("delete %s",url));
                apigrpcFascade.getImageManService().delete(Imageman.DeleteRequest.newBuilder().setUrl(url).build(),
                        new ImageManService.ImageManCallback() {
                            @Override
                            public void onApiReady() {
                                postValue(Status.builder().setCode(Status.Code.START).build());
                            }

                            @Override
                            public void onImageReceived(Common.ImageResponse image) {
                                postValue(Status.builder().setCode(Status.Code.LOADING).build());

                            }

                            @Override
                            public void onImageUpserted(Common.StatusResponse statusResponse) {
                                postValue(Status.builder().setCode(Status.Code.LOADING).build());

                            }

                            @Override
                            public void onImageDeleted(Common.StatusResponse statusResponse) {
                                roomFascade.daoIndexImage.findFlowable(url)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(Schedulers.io())
                                .subscribe(indexImage -> {
                                    Log.i(TAG,String.format("remote delete return , then delete local IndexImage url=%s",url));
                                    roomFascade.daoIndexImage.delete(indexImage);
                                })
                                ;
                                Log.i(TAG, String.format("onImageDeleted(Common.StatusResponse=[%s]", gson.toJson(statusResponse)));
                                postValue(Status.builder().setCode(Status.Code.LOADING).setMessage(gson.toJson(statusResponse)).build());
                            }

                            @Override
                            public void onApiCompleted() {
                                postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                            }

                            @Override
                            public void onApiError() {
                                Log.i(TAG, String.format("call onApiError"));
                                postValue(Status.builder().setCode(Status.Code.END_ERROR).setMessage("api error").build());
                            }

                        });
            }
        };
    }

    public LiveData<Status> upsert(final IndexImage indexImage) {
        return new LiveData<Status>() {
            @Override
            protected void onActive() {
                Imageman.UpsertRequest upsertRequest = Imageman.UpsertRequest
                        .newBuilder()
                        .setUrl(indexImage.url)
                        .setInfoUrl(Strings.isNullOrEmpty(indexImage.infoUrl) ? "" : indexImage.infoUrl)
                        .setTitle(Strings.isNullOrEmpty(indexImage.title) ? "" : indexImage.title)
                        .setType(Strings.isNullOrEmpty(indexImage.type) ? Common.ImageType.SECRET : Common.ImageType.valueOf(indexImage.type))
                        .setActive(indexImage.active)
                        .setToprank(indexImage.toprank)
                        .build();

                apigrpcFascade.getImageManService().

                        upsert(upsertRequest, new ImageManService.ImageManCallback() {
                            @Override
                            public void onApiReady() {
                                postValue(Status.builder().setCode(Status.Code.START).build());
                            }

                            @Override
                            public void onImageReceived(Common.ImageResponse image) {
                                postValue(Status.builder().setCode(Status.Code.LOADING).build());
                            }

                            @Override
                            public void onImageDeleted(Common.StatusResponse value) {
                                postValue(Status.builder().setCode(Status.Code.LOADING).build());
                            }

                            @Override
                            public void onImageUpserted(Common.StatusResponse statusResponse) {
                                Log.i(TAG, String.format("onImageUpserted(Common.StatusResponse=[%s]", gson.toJson(statusResponse)));
                                postValue(Status.builder().setCode(Status.Code.END_SUCCESS).setMessage(gson.toJson(statusResponse)).build());
                            }

                            @Override
                            public void onApiCompleted() {
                                postValue(Status.builder().setCode(Status.Code.END_SUCCESS).build());
                            }

                            @Override
                            public void onApiError() {
                                Log.i(TAG, String.format("call onApiError"));
                                postValue(Status.builder().setCode(Status.Code.END_ERROR).setMessage("api error").build());
                            }
                        });
            }

            @Override
            protected void onInactive() {
                super.onInactive();
            }
        };
    }


    public void saveSampleImageIndices() {
        Observable.just(true).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                    long now = System.currentTimeMillis();
                    Random r = new Random();
                    for (int i = 0; i < 15; ) {
                        roomFascade.daoIndexImage.saveAll(
                                IndexImage.builder()
                                        .setUrl("https://imgcache.cjmx.com/star/201512/20151201213056390.jpg?" + i++)
                                        .setType(Common.ImageType.NORMAL.name())
                                        .setTitle(i + "NORMAL 标题title消灭一切害人虫昵称")
                                        .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                                        .setLastUpdated(now + i)
                                        .setToprank(r.nextBoolean())
                                        .build(),
                                IndexImage.builder()
                                        .setUrl("https://imgcache.cjmx.com/star/201512/20151201213056390.jpg?" + i++)
                                        .setType(Common.ImageType.POSTER.name())
                                        .setTitle(i + "POSTER 标题title消灭一切害人虫昵称")
                                        .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                                        .setLastUpdated(now + i)
                                        .setToprank(r.nextBoolean())
                                        .build(),
                                IndexImage.builder()
                                        .setUrl("https://imgcache.cjmx.com/star/201512/20151201213056390.jpg?" + i++)
                                        .setType(Common.ImageType.SEXY.name())
                                        .setTitle(i + "SEXY 标题title消灭一切害人虫昵称")
                                        .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                                        .setLastUpdated(now + i)
                                        .setToprank(r.nextBoolean())
                                        .build(),
                                IndexImage.builder()
                                        .setUrl("https://imgcache.cjmx.com/star/201512/20151201213056390.jpg?" + i++)
                                        .setType(Common.ImageType.PORN.name())
                                        .setTitle(i + "PORN 标题title消灭一切害人虫昵称")
                                        .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                                        .setLastUpdated(now + i)
                                        .setToprank(r.nextBoolean())
                                        .build(),
                                IndexImage.builder()
                                        .setUrl("https://imgcache.cjmx.com/star/201512/20151201213056390.jpg?" + i++)
                                        .setType(Common.ImageType.SECRET.name())
                                        .setTitle(i + "SECRET 标题title消灭一切害人虫昵称")
                                        .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                                        .setLastUpdated(now + i)
                                        .setToprank(r.nextBoolean())
                                        .build()
                        );

                    }
                });

    }
}