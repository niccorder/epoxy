package com.airbnb.epoxy.util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public final class ImmediateExecutor implements Executor {

  private static final ImmediateExecutor
      INSTANCE = new ImmediateExecutor();

  public static ImmediateExecutor get() {
    return INSTANCE;
  }

  private ImmediateExecutor() {
  }

  @Override
  public void execute(@NonNull Runnable command) {
    command.run();
  }
}
