package org.ditto.lib.apigrpc;


import javax.inject.Inject;

/**
 * Created by amirziarati on 10/4/16.
 */
public class ApigrpcFascade {

    @Inject
    String strAmir;

    ImageManService imageManService;

    @Inject
    public ApigrpcFascade(ImageManService imageManService) {
        this.imageManService = imageManService;
        System.out.println(strAmir);

    }


    public String getConvertedStrAmir() {
        return "Convert " + strAmir;
    }

    public ImageManService getImageManService() {
        return imageManService;
    }
}