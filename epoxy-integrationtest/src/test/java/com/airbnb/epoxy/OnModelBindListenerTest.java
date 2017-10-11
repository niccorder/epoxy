package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;

import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.ModelWithClickListener_;
import com.airbnb.epoxy.util.ImmediateExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OnModelBindListenerTest {

  private ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();
  private TestController testController;

  static class TestController extends EpoxyController {

    private EpoxyModel model;

    TestController() {
      super(ImmediateExecutor.get(), ImmediateExecutor.get());
    }

    @Override
    protected void buildModels() {
      add(model);
    }

    void setModel(EpoxyModel model) {
      this.model = model.id(1);
    }

    void buildWithModel(EpoxyModel model) {
      setModel(model);
      requestModelBuild();
    }
  }

  static class BindListener implements OnModelBoundListener<ModelWithClickListener_, View> {
    boolean called;

    @Override
    public void onModelBound(ModelWithClickListener_ model, View view, int position) {
      called = true;
    }
  }

  static class UnbindListener implements OnModelUnboundListener<ModelWithClickListener_, View> {
    boolean called;

    @Override
    public void onModelUnbound(ModelWithClickListener_ model, View view) {
      called = true;
    }
  }

  @Before
  public void setUp() throws Exception {
    testController = new TestController();
  }

  @Test
  public void onBindListenerGetsCalled() {
    BindListener bindListener = new BindListener();
    ModelWithClickListener_ model = new ModelWithClickListener_().onBind(bindListener);
    testController.setModel(model);

    assertFalse(bindListener.called);
    lifecycleHelper.buildModelsAndBind(testController);
    assertTrue(bindListener.called);
  }

  @Test
  public void onUnbindListenerGetsCalled() {
    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.setModel(model);

    UnbindListener unbindlistener = new UnbindListener();
    model.onUnbind(unbindlistener);

    assertFalse(unbindlistener.called);
    lifecycleHelper.buildModelsAndBind(testController);
    assertFalse(unbindlistener.called);

    lifecycleHelper.recycleLastBoundModel(testController);
    assertTrue(unbindlistener.called);
  }

  @Test
  public void bindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    model = new ModelWithClickListener_();
    model.onBind(null);
    testController.buildWithModel(model);
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    BindListener listener1 = new BindListener();
    model.onBind(listener1);
    testController.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    model.onBind(listener1);
    testController.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void nullBindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onBind(new BindListener());
    testController.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onBind(null);
    testController.buildWithModel(model);

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void newBindListenerDoesNotChangeHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onBind(new BindListener());
    testController.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onBind(new BindListener());
    testController.buildWithModel(model);

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void unbindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    model = new ModelWithClickListener_();
    model.onUnbind(null);
    testController.buildWithModel(model);
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    UnbindListener listener1 = new UnbindListener();
    model.onUnbind(listener1);
    testController.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    model.onUnbind(listener1);
    testController.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void nullUnbindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onUnbind(new UnbindListener());
    testController.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onUnbind(null);
    testController.buildWithModel(model);

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void newUnbindListenerDoesNotChangHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onUnbind(new UnbindListener());
    testController.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onUnbind(new UnbindListener());
    testController.buildWithModel(model);

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), any());
  }
}
