package org.ditto.lib.repository;

import org.ditto.lib.apigrpc.ApigrpcFascade;

import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class RepositoryFascade {


    @Inject
    String strAmir;

    public IndexImageRepository indexImageRepository;
    public IndexVisitorRepository indexVisitorRepository;
    public  UserRepository userRepository;
    public  ApigrpcFascade apigrpcFascade;


    @Inject
    public RepositoryFascade(UserRepository userRepository,
                             IndexImageRepository indexImageRepository,
                             IndexVisitorRepository indexVisitorRepository,
                             ApigrpcFascade apigrpcFascade ) {
        this.userRepository = userRepository;
        this.indexImageRepository = indexImageRepository;
        this.indexVisitorRepository = indexVisitorRepository;
        this.apigrpcFascade = apigrpcFascade;
        System.out.println(strAmir);

    }

    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }

}