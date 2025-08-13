-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(...);
}
-keep class * extends com.heretic_cultivator.super_adapter.holder.ViewBindingViewHolder {
   <init>(...);
}