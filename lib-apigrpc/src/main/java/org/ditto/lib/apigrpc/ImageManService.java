package org.ditto.lib.apigrpc;


import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.apigrpc.model.Image;
import org.ditto.sexyimage.grpc.Common;
import org.ditto.sexyimage.grpc.ImageManGrpc;
import org.ditto.sexyimage.grpc.Imageman;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.ManagedChannel;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;

/**
 * Created by admin on 2017/9/24.
 */

@Singleton
public class ImageManService {

    private final static String TAG = ImageManService.class.getSimpleName();

    public interface ListImageCallback {
        void onImageReceived(Image image);

    }

    private static final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(ImageManService.class.getName());
    private final ManagedChannel channel;
    private final ImageManGrpc.ImageManStub imageManStub;
    private final HealthGrpc.HealthStub healthFutureStub;
    final HealthCheckRequest healthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(ImageManGrpc.getServiceDescriptor().getName())
            .build();

    @Inject
    public ImageManService(final ManagedChannel channel) {
        this.channel = channel;
        imageManStub = ImageManGrpc.newStub(channel);
        healthFutureStub = HealthGrpc.newStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    ClientCallStreamObserver<Imageman.ListRequest> listRequestStream;

    public void listImages(Common.ImageType imageType, long lastUpdated, ListImageCallback callback) {
        healthFutureStub.check(healthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {
                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            imageManStub.withWaitForReady().list(Imageman.ListRequest.newBuilder().setType(imageType).setLastUpdated(lastUpdated).build(), new ListImagesStreamObserver(callback));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("onError grpc service check health\n%s", t.getMessage()));
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("onCompleted grpc service check health\n%s", ""));
                    }
                });

    }



    private class ListImagesStreamObserver implements ClientResponseObserver<
            Imageman.ListRequest, Common.ImageResponse> {


        ListImageCallback listImageCallback;

        public ListImagesStreamObserver(ListImageCallback listImageCallback) {
            this.listImageCallback = listImageCallback;
        }


        @Override
        public void beforeStart(ClientCallStreamObserver requestStream) {
            listRequestStream = requestStream;
            listRequestStream.disableAutoInboundFlowControl();
            listRequestStream.setOnReadyHandler(() -> {
                logger.info(String.format("%s", "setOnReadyHandler isSubscribingImages.set(true)"));
            });
        }

        @Override
        public void onNext(Common.ImageResponse response) {
            Image image = Image
                    .builder()
                    .setUrl(response.getUrl())
                    .setInfoUrl(response.getInfoUrl())
                    .setType(response.getType())
                    .setLastUpdated(response.getLastUpdated())
                    .build();
            listImageCallback.onImageReceived(image);
            listRequestStream.request(1);
            logger.info(String.format("%s image=[%s]", "onNext listRequestStream.request(1)", gson.toJson(image)));
        }


        @Override
        public void onError(Throwable t) {
            logger.info(String.format("%s%s", "onError isSubscribingImages.set(false)", t.getMessage()));
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {
            logger.info(String.format("%s", "onCompleted isSubscribingImages.set(false)"));
        }
    }


    public static Set<Common.ImageType> getImageTypes(boolean normal, boolean poster, boolean sexy, boolean porn) {
        return new HashSet<Common.ImageType>() {
            {
                if (normal) add(Common.ImageType.NORMAL);
                if (poster) add(Common.ImageType.POSTER);
                if (sexy) add(Common.ImageType.SEXY);
                if (porn) add(Common.ImageType.PORN);
            }
        };
    }

}
