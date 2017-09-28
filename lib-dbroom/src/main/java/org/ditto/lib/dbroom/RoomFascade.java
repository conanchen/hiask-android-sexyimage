package org.ditto.lib.dbroom;

import org.ditto.lib.dbroom.index.DaoIndexImage;
import org.ditto.lib.dbroom.index.DaoIndexVisitor;
import org.ditto.lib.dbroom.user.DaoUser;

import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class RoomFascade {

    public final DaoUser daoUser;
    public final DaoIndexImage daoIndexImage;
    public final DaoIndexVisitor daoIndexVisitor;
    @Inject
    String strAmir;


    @Inject
    public RoomFascade(DaoUser daoUser,
                       DaoIndexImage daoIndexImage,
                       DaoIndexVisitor daoIndexVisitor) {
        this.daoUser = daoUser;
        this.daoIndexImage = daoIndexImage;
        this.daoIndexVisitor = daoIndexVisitor;
        System.out.println(strAmir);

    }

    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }


}