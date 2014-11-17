package io.relayr.api;

import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;

import io.relayr.model.Status;
import rx.Observable;

import static io.relayr.api.MockBackend.SERVER_STATUS;

public class MockStatusApi implements StatusApi {

    private final MockBackend mMockBackend;

    @Inject
    public MockStatusApi(MockBackend mockBackend) {
        mMockBackend = mockBackend;
    }

    @Override
    public Observable<Status> getServerStatus() {
        return mMockBackend.createObservable(new TypeToken<Status>(){}, SERVER_STATUS);
    }
}
