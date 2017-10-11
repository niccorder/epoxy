package com.airbnb.epoxy;

import android.support.annotation.VisibleForTesting;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * A small wrapper around {@link com.airbnb.epoxy.EpoxyController} that lets you set a list of
 * models directly.
 */
public class SimpleEpoxyController extends EpoxyController {
  private List<? extends EpoxyModel<?>> currentModels;
  private boolean insideSetModels;

  public SimpleEpoxyController() {
    super();
  }

  public SimpleEpoxyController(Executor diffExecutor) {
    super(diffExecutor);
  }

  @VisibleForTesting
  SimpleEpoxyController(
      Executor diffExecutor,
      Executor notifyExecutor
  ) {
    super(diffExecutor, notifyExecutor);
  }

  /**
   * Set the models to add to this controller. Clears any previous models and adds this new list
   * .
   */
  public void setModels(List<? extends EpoxyModel<?>> models) {
    currentModels = models;
    insideSetModels = true;
    requestModelBuild();
    insideSetModels = false;
  }

  @Override
  public final void requestModelBuild() {
    if (!insideSetModels) {
      throw new IllegalEpoxyUsage(
          "You cannot call `requestModelBuild` directly. Call `setModels` instead.");
    }
    super.requestModelBuild();
  }

  @Override
  protected final void buildModels() {
    if (!isBuildingModels()) {
      throw new IllegalEpoxyUsage(
          "You cannot call `buildModels` directly. Call `setModels` instead.");
    }
    add(currentModels);
  }
}
