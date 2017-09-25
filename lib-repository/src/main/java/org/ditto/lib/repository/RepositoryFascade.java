package org.ditto.lib.repository;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apirest.ApirestFascade;

import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class RepositoryFascade {


    @Inject
    String strAmir;

    public BuyanswerRepository buyanswerRepository;
    public IndexImageRepository indexImageRepository;
    public IndexVisitorRepository indexVisitorRepository;
    public  UserRepository userRepository;
    public  ApigrpcFascade apigrpcFascade;
    public  ApirestFascade apirestFascade;


    @Inject
    public RepositoryFascade(UserRepository userRepository,
                             BuyanswerRepository buyanswerRepository,
                             IndexImageRepository indexImageRepository,
                             IndexVisitorRepository indexVisitorRepository,
                             ApigrpcFascade apigrpcFascade,
                             ApirestFascade apirestFascade ) {
        this.userRepository = userRepository;
        this.buyanswerRepository = buyanswerRepository;
        this.indexImageRepository = indexImageRepository;
        this.indexVisitorRepository = indexVisitorRepository;
        this.apigrpcFascade = apigrpcFascade;
        this.apirestFascade = apirestFascade;
        System.out.println(strAmir);

    }

    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }

}