package org.ditto.lib.repository;

import android.arch.lifecycle.LiveData;

import org.ditto.lib.apirest.ApirestFascade;
import org.ditto.lib.apirest.util.AppExecutors;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.image.Buyanswer;
import org.ditto.lib.dbroom.index.IndexVisitor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository that handles VoUser objects.
 */
@Singleton
public class IndexVisitorRepository {

    private ApirestFascade githubService;
    private final AppExecutors appExecutors;
    private RoomFascade roomFascade;

    @Inject
    public IndexVisitorRepository(ApirestFascade githubService, RoomFascade roomFascade) {
        this.roomFascade = roomFascade;
        this.githubService = githubService;
        this.appExecutors = new AppExecutors();
    }

    public LiveData<List<IndexVisitor>> listVisitorsBy(int size) {

        return roomFascade.daoIndexVisitor.listPartyIndicesBy(size);
    }

    public void saveSampleVisitorIndices() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < 30; ) {
            roomFascade.daoIndexVisitor.saveAll(
                    IndexVisitor.builder()
                            .setUuid("uuid" + i++)
                            .setType(Buyanswer.class.getSimpleName())
                            .setTitle(i + "IndexVisitor BuyanswerMessage 标题title消灭一切害人虫昵称")
                            .setDetail(i + "详细detail深入理解ConstraintLayout之使用姿势约束是一种规则," +
                                    "用来表示视图之间的相对关系约束是一种规则,用来表示视图之间的相对关系用来表示视图" +
                                    "之间的相对关系约束是一种规则,用来表示视图之间的相对关系")
                            .setLastUpdated(now + i)
                            .build()
            );
        }
    }


}