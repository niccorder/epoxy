package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Deprecated;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
@Deprecated
public class ModelWithAnnotation_ extends ModelWithAnnotation implements GeneratedModel<Object>, ModelWithAnnotationBuilder {
  private OnModelBoundListener<ModelWithAnnotation_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithAnnotation_, Object> onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithAnnotation_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object, int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void handlePostBind(final Object object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithAnnotation_ onBind(OnModelBoundListener<ModelWithAnnotation_, Object> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(Object object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithAnnotation_ onUnbind(OnModelUnboundListener<ModelWithAnnotation_, Object> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public ModelWithAnnotation_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithAnnotation_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public ModelWithAnnotation_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ModelWithAnnotation_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithAnnotation_ id(CharSequence key, CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public ModelWithAnnotation_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithAnnotation_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithAnnotation_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public ModelWithAnnotation_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithAnnotation_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithAnnotation_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithAnnotation_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithAnnotation_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithAnnotation_ that = (ModelWithAnnotation_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithAnnotation_{" +
        "}" + super.toString();
  }
}