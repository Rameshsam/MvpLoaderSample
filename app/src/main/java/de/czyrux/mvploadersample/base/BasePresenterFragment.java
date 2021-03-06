package de.czyrux.mvploadersample.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

public abstract class BasePresenterFragment<P extends Presenter<V>, V> extends Fragment implements
        LoaderManager.LoaderCallbacks<P> {

    private static final String TAG = "base-fragment";
    private static final int LOADER_ID = 101;

    // boolean flag to avoid delivering the result twice. Calling initLoader in onActivityCreated makes
    // onLoadFinished will be called twice during configuration change.
    private boolean delivered = false;
    private Presenter<V> presenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated-" + tag());
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume-" + tag());
        presenter.onViewAttached(getPresenterView());
    }

    @Override
    public void onPause() {
        presenter.onViewDetached();
        super.onPause();
        Log.i(TAG, "onPause-" + tag());
    }

    @Override
    public final Loader<P> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader-" + tag());
        return new PresenterLoader<>(getContext(), getPresenterFactory(), tag());
    }

    @Override
    public final void onLoadFinished(Loader<P> loader, P presenter) {
        Log.i(TAG, "onLoadFinished-" + tag());
        if (!delivered) {
            this.presenter = presenter;
            delivered = true;
            onPresenterPrepared(presenter);
        }
    }

    @Override
    public final void onLoaderReset(Loader<P> loader) {
        Log.i(TAG, "onLoaderReset-" + tag());
        presenter = null;
        onPresenterDestroyed();
    }

    protected abstract String tag();

    protected abstract PresenterFactory<P> getPresenterFactory();

    protected abstract void onPresenterPrepared(P presenter);

    protected final void onPresenterDestroyed() {
        // hook for subclasses
    }

    // Override in case of fragment not implementing Presenter<View> interface
    protected V getPresenterView() {return (V) this;}
}
