package com.csipsimple.widgets;

// TODO, NNNN: Remove this file
interface View_HasStateListenerSupport {
    /* (non-Javadoc)
     * @see com.actionbarsherlock.internal.view.View_HasStateListenerSupport#addOnAttachStateChangeListener(com.actionbarsherlock.internal.view.View_OnAttachStateChangeListener)
     */
    void addOnAttachStateChangeListener(View_OnAttachStateChangeListener listener);

    /* (non-Javadoc)
     * @see com.actionbarsherlock.internal.view.View_HasStateListenerSupport#removeOnAttachStateChangeListener(com.actionbarsherlock.internal.view.View_OnAttachStateChangeListener)
     */
    void removeOnAttachStateChangeListener(View_OnAttachStateChangeListener listener);
}
