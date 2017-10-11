package com.airbnb.epoxy;

import com.airbnb.epoxy.util.ImmediateExecutor;

class TestAdapter extends EpoxyAdapter {

  TestAdapter() {
    enableDiffing(ImmediateExecutor.get());
  }
}
