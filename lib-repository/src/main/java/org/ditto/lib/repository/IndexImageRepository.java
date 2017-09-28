package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.ImageManService;
import org.ditto.lib.apigrpc.model.Image;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.sexyimage.grpc.Common;
import org.ditto.sexyimage.grpc.Imageman;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

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


    public LiveData<List<IndexImage>> listImagesBy(int size) {
        apigrpcFascade.getImageManService().listImages(Common.ImageType.NORMAL, 0, new ImageManService.ImageManCallback() {
            @Override
            public void onImageReceived(Image image) {
                Log.i(TAG, String.format("onImageReceived save to database, image.url=%s\n image=[%s]", image.url, gson.toJson(image)));
                IndexImage indexImage = IndexImage.builder()
                        .setUrl(image.url)
                        .setInfoUrl(image.infoUrl)
                        .setTitle(image.title)
                        .setDesc(image.desc)
                        .setType(image.type.name())
                        .setLastUpdated(image.lastUpdated)
                        .build();
                roomFascade.daoIndexImage.save(indexImage);
            }

            @Override
            public void onImageDeleted(Common.StatusResponse value) {

            }


            @Override
            public void onImageUpserted(Common.StatusResponse statusResponse) {

            }
        });
        return roomFascade.daoIndexImage.listImageIndicesBy(size);
    }


    public LiveData<IndexImage> find(String imageUrl) {
        return roomFascade.daoIndexImage.find(imageUrl);
    }


    public void delete(String url) {
        apigrpcFascade.getImageManService().delete(Imageman.DeleteRequest.newBuilder().setUrl(url).build(),
                new ImageManService.ImageManCallback() {
                    @Override
                    public void onImageReceived(Image image) {

                    }

                    @Override
                    public void onImageUpserted(Common.StatusResponse statusResponse) {

                    }

                    @Override
                    public void onImageDeleted(Common.StatusResponse statusResponse) {
                        Log.i(TAG, String.format("onImageDeleted(Common.StatusResponse=[%s]", gson.toJson(statusResponse)));
                    }
                });
    }

    public void upsert(IndexImage indexImage) {
        Imageman.UpsertRequest upsertRequest = Imageman.UpsertRequest
                .newBuilder()
                .setUrl(indexImage.url)
                .setInfoUrl(Strings.isNullOrEmpty(indexImage.infoUrl) ? "" : indexImage.infoUrl)
                .setTitle(Strings.isNullOrEmpty(indexImage.title) ? "" : indexImage.title)
                .setType(Strings.isNullOrEmpty(indexImage.type) ? Common.ImageType.SECRET : Common.ImageType.valueOf(indexImage.type))
                .setActive(indexImage.active)
                .setToprank(indexImage.toprank)
                .build();
        apigrpcFascade.getImageManService().upsert(upsertRequest, new ImageManService.ImageManCallback() {
            @Override
            public void onImageReceived(Image image) {

            }

            @Override
            public void onImageDeleted(Common.StatusResponse value) {

            }

            @Override
            public void onImageUpserted(Common.StatusResponse statusResponse) {
                Log.i(TAG, String.format("onImageUpserted(Common.StatusResponse=[%s]", gson.toJson(statusResponse)));
            }
        });
    }


    public void saveSampleImageIndices() {
        long now = System.currentTimeMillis();
        Random r = new Random();
        for (int i = 0; i < 30; ) {
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
    }
}