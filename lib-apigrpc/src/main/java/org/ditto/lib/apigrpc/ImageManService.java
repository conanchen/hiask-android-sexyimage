package org.ditto.lib.apigrpc;


import android.util.Log;

import com.google.gson.Gson;

import org.ditto.sexyimage.common.grpc.ImageResponse;
import org.ditto.sexyimage.common.grpc.ImageType;
import org.ditto.sexyimage.common.grpc.StatusResponse;
import org.ditto.sexyimage.manage.grpc.DeleteRequest;
import org.ditto.sexyimage.manage.grpc.ImageManGrpc;
import org.ditto.sexyimage.manage.grpc.ListRequest;
import org.ditto.sexyimage.manage.grpc.UpsertRequest;

import java.util.HashSet;
import java.util.Set;
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

    public interface ImageManCallback {
        void onImageReceived(ImageResponse image);

        void onImageUpserted(StatusResponse statusResponse);

        void onImageDeleted(StatusResponse statusResponse);

        void onApiError();

        void onApiCompleted();

        void onApiReady();
    }

    private static final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(ImageManService.class.getName());
    private final ManagedChannel channel;
    private final ImageManGrpc.ImageManStub imageManStub;
    private final HealthGrpc.HealthStub healthStub;
    final HealthCheckRequest healthCheckRequest = HealthCheckRequest
            .newBuilder()
            .setService(ImageManGrpc.getServiceDescriptor().getName())
            .build();

    @Inject
    public ImageManService(final ManagedChannel channel) {
        this.channel = channel;
        imageManStub = ImageManGrpc.newStub(channel);
        healthStub = HealthGrpc.newStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    ClientCallStreamObserver<ListRequest> listRequestStream;

    public void listImages(ListRequest listRequest, ImageManCallback callback) {
        callback.onApiReady();
        healthStub.check(healthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {

                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            Log.i(TAG, String.format("imageManStub.withWaitForReady().list() listRequest = [%s]", gson.toJson(listRequest)));
                            imageManStub.withWaitForReady().list(listRequest,
                                    new ListImagesStreamObserver(callback));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("onCompleted grpc service check health\n%s", ""));
                        callback.onApiCompleted();
                    }
                });

    }

    public void delete(DeleteRequest deleteRequest, ImageManCallback callback) {
        Log.i(TAG, String.format("before healthcheck deleteRequest=[%s]", gson.toJson(deleteRequest)));
        healthStub.check(healthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {
                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            imageManStub.withWaitForReady().delete(deleteRequest, new StreamObserver<StatusResponse>() {
                                @Override
                                public void onNext(StatusResponse response) {
                                    Log.i(TAG, String.format("delete callback StatusResponse=[%s]", gson.toJson(response)));
                                    callback.onImageDeleted(response);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    callback.onApiError();
                                }

                                @Override
                                public void onCompleted() {
                                    callback.onApiCompleted();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("onCompleted grpc service check health\n%s", ""));
                    }
                });

    }

    public void upsert(UpsertRequest upsertRequest, ImageManCallback callback) {
        healthStub.check(healthCheckRequest,
                new StreamObserver<HealthCheckResponse>() {
                    @Override
                    public void onNext(HealthCheckResponse value) {
                        if (value.getStatus() == HealthCheckResponse.ServingStatus.SERVING) {
                            imageManStub.withWaitForReady().upsert(upsertRequest, new StreamObserver<StatusResponse>() {
                                @Override
                                public void onNext(StatusResponse value) {
                                    callback.onImageUpserted(value);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    callback.onApiError();

                                }

                                @Override
                                public void onCompleted() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, String.format("onError grpc service check health\n%s", t.getMessage()));
                        callback.onApiError();
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, String.format("onCompleted grpc service check health\n%s", ""));
                    }
                });

    }


    private class ListImagesStreamObserver implements ClientResponseObserver<
            ListRequest, ImageResponse> {


        ImageManCallback imageManCallback;

        public ListImagesStreamObserver(ImageManCallback imageManCallback) {
            this.imageManCallback = imageManCallback;
        }

        @Override
        public void beforeStart(ClientCallStreamObserver requestStream) {
            listRequestStream = requestStream;
            listRequestStream.disableAutoInboundFlowControl();
            listRequestStream.setOnReadyHandler(() -> {
                logger.info(String.format("%s", "setOnReadyHandler isSubscribingImages.set(true)"));
                imageManCallback.onApiReady();
            });
        }

        @Override
        public void onNext(ImageResponse response) {
            imageManCallback.onImageReceived(response);
            listRequestStream.request(1);
            logger.info(String.format("%s image=[%s]", "onNext listRequestStream.request(1)", gson.toJson(response)));
        }


        @Override
        public void onError(Throwable t) {
            logger.info(String.format("%s%s", "onError isSubscribingImages.set(false)", t.getMessage()));
            t.printStackTrace();
            imageManCallback.onApiError();
        }

        @Override
        public void onCompleted() {
            logger.info(String.format("%s", "onCompleted isSubscribingImages.set(false)"));
            imageManCallback.onApiCompleted();
        }
    }


    public static Set<ImageType> getImageTypes(boolean normal, boolean poster, boolean sexy, boolean porn) {
        return new HashSet<ImageType>() {
            {
                if (normal) add(ImageType.NORMAL);
                if (poster) add(ImageType.POSTER);
                if (sexy) add(ImageType.SEXY);
                if (porn) add(ImageType.PORN);
            }
        };
    }

}
