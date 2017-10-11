package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;
import android.view.View.OnClickListener;

import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.ModelWithClickListener_;
import com.airbnb.epoxy.integrationtest.ModelWithLongClickListener_;
import com.airbnb.epoxy.util.ImmediateExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ModelClickListenerTest {

  private ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();
  private TestController testController;

  static class TestController extends EpoxyController {
    private EpoxyModel<?> model;

    TestController() {
      super(ImmediateExecutor.get(), ImmediateExecutor.get());
    }

    @Override
    protected void buildModels() {
      add(model.id(1));
    }

    void setModel(EpoxyModel<?> model) {
      this.model = model;
    }
  }

  static class ModelClickListener implements OnModelClickListener<ModelWithClickListener_, View> {
    boolean clicked;

    @Override
    public void onClick(ModelWithClickListener_ model, View view, View v, int position) {
      clicked = true;
    }
  }

  static class ModelLongClickListener implements OnModelLongClickListener<ModelWithLongClickListener_, View> {
    boolean clicked;

    @Override
    public boolean onLongClick(ModelWithLongClickListener_ model, View view, View v, int position) {
      clicked = true;
      return true;
    }
  }

  static class ViewClickListener implements OnClickListener {
    boolean clicked;

    @Override
    public void onClick(View v) {
      clicked = true;
    }
  }

  @Before
  public void setUp() throws Exception {
    testController = new TestController();
  }

  @Test
  public void basicModelClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    model.clickListener(modelClickListener);

    testController.setModel(model);

    lifecycleHelper.buildModelsAndBind(testController);

    View view = new View(RuntimeEnvironment.application);
    model.clickListener().onClick(view);
    assertTrue(modelClickListener.clicked);

    verify(modelClickListener)
        .onClick(eq(model), any(View.class), nullable(View.class), anyInt());
  }

  @Test
  public void basicModelLongClickListener() {
    final ModelWithLongClickListener_ model = new ModelWithLongClickListener_();
    ModelLongClickListener modelClickListener = spy(new ModelLongClickListener());
    model.clickListener(modelClickListener);

    testController.setModel(model);

    lifecycleHelper.buildModelsAndBind(testController);

    View view = new View(RuntimeEnvironment.application);
    model.clickListener().onLongClick(view);
    assertTrue(modelClickListener.clicked);

    verify(modelClickListener)
        .onLongClick(eq(model), any(View.class), nullable(View.class), anyInt());
  }

  @Test
  public void modelClickListenerOverridesViewClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();

    testController.setModel(model);

    ViewClickListener viewClickListener = new ViewClickListener();
    model.clickListener(viewClickListener);
    assertNotNull(model.clickListener());

    ModelClickListener modelClickListener = new ModelClickListener();
    model.clickListener(modelClickListener);
    assertNotSame(model.clickListener(), viewClickListener);

    lifecycleHelper.buildModelsAndBind(testController);
    assertNotNull(model.clickListener());

    model.clickListener().onClick(null);
    assertTrue(modelClickListener.clicked);
    assertFalse(viewClickListener.clicked);
  }

  @Test
  public void viewClickListenerOverridesModelClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();

    testController.setModel(model);

    ModelClickListener modelClickListener = new ModelClickListener();
    model.clickListener(modelClickListener);

    ViewClickListener viewClickListener = new ViewClickListener();
    model.clickListener(viewClickListener);

    lifecycleHelper.buildModelsAndBind(testController);
    assertNotNull(model.clickListener());

    model.clickListener().onClick(null);
    assertTrue(viewClickListener.clicked);
    assertFalse(modelClickListener.clicked);
  }

  @Test
  public void resetClearsModelClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();

    testController.setModel(model);

    ModelClickListener modelClickListener = spy(new ModelClickListener());
    model.clickListener(modelClickListener);
    model.reset();

    lifecycleHelper.buildModelsAndBind(testController);
    assertNull(model.clickListener());
  }

  @Test
  public void modelClickListenerIsDiffed() {
    // Internally we wrap the model click listener with an anonymous click listener. We can't hash
    // the anonymous click listener since that changes the model state, instead our anonymous
    // click listener should use the hashCode of the user's click listener

    ModelClickListener modelClickListener = new ModelClickListener();
    ViewClickListener viewClickListener = new ViewClickListener();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.setModel(model);
    testController.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.clickListener(modelClickListener);
    testController.setModel(model);
    testController.requestModelBuild();

    // The second update shouldn't cause a item change
    model = new ModelWithClickListener_();
    model.clickListener(modelClickListener);
    testController.setModel(model);
    testController.requestModelBuild();

    model = new ModelWithClickListener_();
    model.clickListener(viewClickListener);
    testController.setModel(model);
    testController.requestModelBuild();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
    verifyNoMoreInteractions(observerMock);
  }

  @Test
  public void viewClickListenerIsDiffed() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    testController.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    testController.setModel(model);
    testController.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    ViewClickListener viewClickListener = new ViewClickListener();
    model = new ModelWithClickListener_();
    model.clickListener(viewClickListener);
    testController.setModel(model);
    testController.requestModelBuild();

    // The second update shouldn't cause a item change
    model = new ModelWithClickListener_();
    model.clickListener(viewClickListener);
    testController.setModel(model);
    testController.requestModelBuild();

    ModelClickListener modelClickListener = new ModelClickListener();
    model = new ModelWithClickListener_();
    model.clickListener(modelClickListener);
    testController.setModel(model);
    testController.requestModelBuild();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
    verifyNoMoreInteractions(observerMock);
  }
}
