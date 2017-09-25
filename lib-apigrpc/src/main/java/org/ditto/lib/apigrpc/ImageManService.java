package org.ditto.lib.apigrpc;


import com.google.gson.Gson;

import org.ditto.lib.apigrpc.model.Image;
import org.ditto.sexyimage.grpc.Common;
import org.ditto.sexyimage.grpc.ImageManGrpc;
import org.ditto.sexyimage.grpc.Imageman;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.grpc.ManagedChannel;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;

/**
 * Created by admin on 2017/9/24.
 */

@Singleton
public class ImageManService {


    public interface ListImageCallback {
        void onImageReceived(Image image);
    }

    private static final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(ImageManService.class.getName());
    private final ManagedChannel channel;
    private final ImageManGrpc.ImageManStub asyncStub;

    @Inject
    public ImageManService(final ManagedChannel channel) {
        this.channel = channel;
        asyncStub = ImageManGrpc.newStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    ClientCallStreamObserver<Imageman.ListRequest> listRequestStream;

    public void listImages(Common.ImageType imageType, long lastUpdated, ListImageCallback callback) {
        asyncStub.withWaitForReady().list(Imageman.ListRequest.newBuilder().setType(imageType).setLastUpdated(lastUpdated).build(), new ListImagesStreamObserver(callback));
    }

    public boolean isHealth() {
        final HealthCheckRequest healthCheckRequest = HealthCheckRequest
                .newBuilder()
                .setService(ImageManGrpc.getServiceDescriptor().getName())
                .build();
        final HealthGrpc.HealthFutureStub healthFutureStub = HealthGrpc.newFutureStub(channel);
        final HealthCheckResponse.ServingStatus servingStatus;
        try {
            servingStatus = healthFutureStub.check(healthCheckRequest).get().getStatus();
            return HealthCheckResponse.ServingStatus.SERVING.equals(servingStatus);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
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
            logger.info(String.format("%s image=[%s]", "onNext listRequestStream.request(1)",gson.toJson(image)));
        }


        @Override
        public void onError(Throwable t) {
            logger.info(String.format("%s%s", "onError isSubscribingImages.set(false)",t.getMessage()));
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
