package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

import com.airbnb.epoxy.EpoxyController.Interceptor;
import com.airbnb.epoxy.util.ImmediateExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class EpoxyControllerTest {

  List<EpoxyModel<?>> savedModels;
  private TestEpoxyController testController;

  private static class TestEpoxyController extends EpoxyController {

    private Runnable buildModelAction;
    boolean exceptionSwallowed = false;

    TestEpoxyController() {
      super(ImmediateExecutor.get());
    }

    void setBuildModelAction(Runnable buildModelAction) {
      this.buildModelAction = buildModelAction;
    }

    @Override
    protected void buildModels() {
      buildModelAction.run();
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
      super.onExceptionSwallowed(exception);
      exceptionSwallowed = true;
    }
  }

  @Before
  public void setUp() throws Exception {
    testController = new TestEpoxyController();

    assertFalse(testController.exceptionSwallowed);
  }

  @Test
  public void basicBuildModels() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);

    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(new TestModel());
      }
    });
    testController.getAdapter().registerAdapterDataObserver(observer);
    testController.requestModelBuild();

    assertEquals(1, testController.getAdapter().getItemCount());
    verify(observer).onItemRangeInserted(0, 1);
    verifyNoMoreInteractions(observer);
  }

  @Test(expected = IllegalStateException.class)
  public void addingSameModelTwiceThrows() {
    final TestModel model = new TestModel();
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(model);
        testController.add(model);
      }
    });

    testController.requestModelBuild();
  }

  @Test
  public void filterDuplicates() {
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(TestModel.of(1), TestModel.of(1));
      }
    });

    testController.setFilterDuplicates(true);
    testController.requestModelBuild();

    assertEquals(1, testController.getAdapter().getItemCount());
  }

  @Test(expected = IllegalStateException.class)
  public void throwOnDuplicatesIfNotFiltering() {
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(TestModel.of(1), TestModel.of(1));
      }
    });

    testController.requestModelBuild();
  }

  @Test
  public void exceptionSwallowedWhenDuplicateFiltered() {
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(TestModel.of(1), TestModel.of(1));
      }
    });

    testController.setFilterDuplicates(true);
    testController.requestModelBuild();

    assertTrue(testController.exceptionSwallowed);
  }

  @Test
  public void interceptorRunsAfterBuildModels() {
    final TestModel testModel = new TestModel();
    final Interceptor interceptor = spy(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        assertEquals(1, models.size());
      }
    });

    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(new TestModel());
      }
    });
    testController.addInterceptor(interceptor);
    testController.requestModelBuild();

    verify(interceptor).intercept(ArgumentMatchers.<EpoxyModel<?>>anyList());
    assertEquals(1, testController.getAdapter().getItemCount());
  }

  @Test
  public void interceptorCanAddModels() {
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(new TestModel());
      }
    });
    testController.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        models.add(new TestModel());
      }
    });

    testController.requestModelBuild();

    assertEquals(2, testController.getAdapter().getItemCount());
  }

  @Test(expected = IllegalStateException.class)
  public void savedModelsCannotBeAddedToLater() {
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(new TestModel());
      }
    });

    testController.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        savedModels = models;
      }
    });

    testController.requestModelBuild();
    savedModels.add(new TestModel());
  }

  @Test
  public void interceptorCanModifyModels() {
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(new TestModel());
      }
    });

    testController.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        TestModel model = ((TestModel) models.get(0));
        model.value(model.value() + 1);
      }
    });

    testController.requestModelBuild();
  }

  @Test
  public void interceptorsRunInOrderAdded() {
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(new TestModel());
      }
    });

    testController.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        assertEquals(1, models.size());
        models.add(new TestModel());
      }
    });

    testController.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        assertEquals(2, models.size());
        models.add(new TestModel());
      }
    });

    testController.requestModelBuild();

    assertEquals(3, testController.getAdapter().getItemCount());
  }

  @Test
  public void moveModel() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);
    final List<TestModel> testModels = TestModel.listOf(1, 2, 3);
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(testModels);
      }
    });

    EpoxyControllerAdapter adapter = testController.getAdapter();
    adapter.registerAdapterDataObserver(observer);
    testController.requestModelBuild();

    verify(observer).onItemRangeInserted(0, 3);

    testModels.add(0, testModels.remove(1));

    testController.moveModel(1, 0);
    verify(observer).onItemRangeMoved(1, 0, 1);

    assertEquals(testModels, adapter.getCurrentModels());

    testController.requestModelBuild();
    assertEquals(testModels, adapter.getCurrentModels());
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void moveModelOtherWay() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);
    final List<TestModel> testModels = TestModel.listOf(1, 2, 3);
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(testModels);
      }
    });

    EpoxyControllerAdapter adapter = testController.getAdapter();
    adapter.registerAdapterDataObserver(observer);
    testController.requestModelBuild();

    verify(observer).onItemRangeInserted(0, 3);

    testModels.add(2, testModels.remove(1));

    testController.moveModel(1, 2);
    verify(observer).onItemRangeMoved(1, 2, 1);

    assertEquals(testModels, adapter.getCurrentModels());

    testController.requestModelBuild();
    assertEquals(testModels, adapter.getCurrentModels());
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void multipleMoves() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);
    final List<TestModel> testModels = TestModel.listOf(1, 2, 3);
    testController.setBuildModelAction(new Runnable() {
      @Override
      public void run() {
        testController.add(testModels);
      }
    });

    EpoxyControllerAdapter adapter = testController.getAdapter();
    adapter.registerAdapterDataObserver(observer);
    testController.requestModelBuild();

    testModels.add(0, testModels.remove(1));
    testController.moveModel(1, 0);
    verify(observer).onItemRangeMoved(1, 0, 1);

    testModels.add(2, testModels.remove(1));
    testController.moveModel(1, 2);
    verify(observer).onItemRangeMoved(1, 2, 1);

    assertEquals(testModels, adapter.getCurrentModels());
    testController.requestModelBuild();
    assertEquals(testModels, adapter.getCurrentModels());
  }
}