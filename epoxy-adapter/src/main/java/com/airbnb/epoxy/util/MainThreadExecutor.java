package com.airbnb.epoxy.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public final class MainThreadExecutor implements Executor {

  private static final class Holder {
    private static final MainThreadExecutor INSTANCE = new MainThreadExecutor();
  }

  public static MainThreadExecutor get() {
    return Holder.INSTANCE;
  }

  private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

  private MainThreadExecutor() {
  }

  @Override
  public void execute(@NonNull Runnable command) {
    MAIN_HANDLER.post(command);
  }
}
