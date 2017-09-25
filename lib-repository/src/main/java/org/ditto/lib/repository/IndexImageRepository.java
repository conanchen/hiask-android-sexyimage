package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.ImageManService;
import org.ditto.lib.apigrpc.model.Image;
import org.ditto.lib.apirest.ApirestFascade;
import org.ditto.lib.apirest.util.AppExecutors;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.sexyimage.grpc.Common;

import java.util.List;

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
    private ApirestFascade apirestFascade;
    private final AppExecutors appExecutors;
    private RoomFascade roomFascade;

    @Inject
    public IndexImageRepository(ApirestFascade apirestFascade, ApigrpcFascade apigrpcFascade, RoomFascade roomFascade) {
        this.roomFascade = roomFascade;
        this.apirestFascade = apirestFascade;
        this.apigrpcFascade = apigrpcFascade;
        this.appExecutors = new AppExecutors();
    }


    public LiveData<List<IndexImage>> listImagesBy(int size) {
        //TODO: refresh data from server using grpc api
        if (apigrpcFascade.getImageManService().isHealth()) {
            apigrpcFascade.getImageManService().listImages(Common.ImageType.NORMAL, 0,
                    new ImageManService.ListImageCallback() {
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
                    });
        } else {
            Log.i(TAG, String.format("onImageReceived grpc service check health=%b", apigrpcFascade.getImageManService().isHealth()));
        }
        return roomFascade.daoIndexImage.listImageIndicesBy(size);
    }


    public void saveSampleImageIndices() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < 30; ) {
            roomFascade.daoIndexImage.saveAll(
                    IndexImage.builder()
                            .setUrl("uuid" + i++)
                            .setType(Common.ImageType.NORMAL.name())
                            .setTitle(i + "NORMAL 标题title消灭一切害人虫昵称")
                            .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                            .setLastUpdated(now + i)
                            .build(),
                    IndexImage.builder()
                            .setUrl("uuid" + i++)
                            .setType(Common.ImageType.POSTER.name())
                            .setTitle(i + "POSTER 标题title消灭一切害人虫昵称")
                            .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                            .setLastUpdated(now + i)
                            .build(),
                    IndexImage.builder()
                            .setUrl("uuid" + i++)
                            .setType(Common.ImageType.SEXY.name())
                            .setTitle(i + "SEXY 标题title消灭一切害人虫昵称")
                            .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                            .setLastUpdated(now + i)
                            .build(),
                    IndexImage.builder()
                            .setUrl("uuid" + i++)
                            .setType(Common.ImageType.PORN.name())
                            .setTitle(i + "PORN 标题title消灭一切害人虫昵称")
                            .setDesc(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则,用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                            .setLastUpdated(now + i)
                            .build()
            );
        }
    }
}