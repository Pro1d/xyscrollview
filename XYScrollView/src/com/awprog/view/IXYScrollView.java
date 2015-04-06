package com.awprog.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;


/**
 * An improved version of XYScrollView.
 * An awesome horizontal and vertical ScrollView based
 * on android's default views. It can contain only one child
 * as for the android default ScrollView.
 * 
 * API requirement : This view requires at least the API level 9 (Android 1.5)
 * 
 * @author Proïd
 *
 */
public class IXYScrollView extends FrameLayout {
	/** 
	 * This view permit to scroll both horizontally an vertically.
	 * It works with a combination of the android ScrollView and
	 * HorizontalScrollView. With default event handling the two
	 * views cannot scroll at the same time. That is why we need to
	 * override the onInterceptTouchEvent methods to handle the event
	 * as we wish.
	 * 
	 * Structure :
	 *  VerticalScrollView {
	 * 		FrameLayoutItermediate {
	 * 			HorizontalScrollView {
	 * 				FrameLayout {
	 * 					[Content ViewGroup]
	 * 				}
	 * 			}
	 * 		}
	 *  }
	 *  
	 */
	
	/** For horizontal scroll */
	MyHorizontalScrollView mHorizontalScrollView;
	/** For vertical scroll */
	MyVerticalScrollView mVerticalScrollView;
	/** To contain the views added by the user */
	FrameLayout mFinalFrame;
	/** An intermediate FrameLayout to handle the touch event easily **/
	MyFrameLayoutIntermediate mIntermediateFrame;
	
	
	/** Constructor calling initScrolls(). The constructor with 2
	 * params will be call when the view is loaded from a xml file **/
	
	public IXYScrollView(Context context) {
		super(context);
		initStructure();
	}
	public IXYScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initStructure();
	}
	public IXYScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initStructure();
	}
	
	/** Build the structure of the view with the personnalized views **/
	private void initStructure() {
		mVerticalScrollView = new MyVerticalScrollView(getContext());
		
		mIntermediateFrame = new MyFrameLayoutIntermediate(getContext());
		
		mHorizontalScrollView = new MyHorizontalScrollView(getContext());
		
		mFinalFrame = new FrameLayout(getContext());
		
		/** The horizontal scroll bar is not always visible (due to vertical
		 scroll), it is better to hide it definitively. **/
		mHorizontalScrollView.setHorizontalScrollBarEnabled(false);
		mVerticalScrollView.setVerticalScrollBarEnabled(false);
		
		/** For the same reason, we can disable the over scroll effect.
		 However, we need the minimum API level requirement to be increase to level 9 **/
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
			mHorizontalScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
			mVerticalScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		
		
		mVerticalScrollView.addView(mIntermediateFrame);
		mIntermediateFrame.addView(mHorizontalScrollView);
		mHorizontalScrollView.addView(mFinalFrame);
		
		// Call the super addView method. The local method is no longer available
		super.addView(mVerticalScrollView, 0,
				new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/**
	 * Override all the addView methods. The child views are added to
	 * the final FrameLayout. It works well when this view is loaded from
	 * a xml file with one child. Need to be tested for other cases.
	 */
	
	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		mFinalFrame.addView(child);
	}
	@Override
	public void addView(View child) {
		mFinalFrame.addView(child);
	}
	@Override
	public void addView(View child, int index) {
		mFinalFrame.addView(child, index);
	}
	@Override
	public void addView(View child, int width, int height) {
		mFinalFrame.addView(child, width, height);
	}
	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		mFinalFrame.addView(child, params);
	}
	
	/**** Scroll listener **** 
	 * There is not OnScrollChanged Listener in the
	 * standard android library. We add it to join the scroll
	 * position of both VerticalScrollView and HorizontalScrollView
	 * in one function.
	 **/
	
	/** Store the scroll position **/
	private int scrollX = 0, scrollY = 0;
	
	/** The listener given by the user **/
	OnScrollChangedListener scrollListener;
	
	/** Set the scroll changed listener **/
	public void setOnScrollChangedListener(OnScrollChangedListener scrollListener) {
		this.scrollListener = scrollListener;
	}
	
	/** Call by the HorizontalScrollView when its scroll position has changed **/
	private void onScrollXChanged(int x) {
		if(scrollListener != null)
			scrollListener.onScrollChanged(x, scrollY, scrollX, scrollY);
	}
	
	/** Call by the VerticalScrollView when its scroll position has changed **/
	private void onScrollYChanged(int y) {
		if(scrollListener != null)
			scrollListener.onScrollChanged(scrollX, y, scrollX, scrollY);
	}
	
	/** An interface that gives an access to the changes of the scroll position  **/
	public interface OnScrollChangedListener {
		public void onScrollChanged(int x, int y, int oldX, int oldY);
	}
	

	/**** Over scroll listener ***
	 * Same as scroll listener...
	 **/
	
	/** The listener given by the user **/
	OnOverScrollListener overScrollListener;
	
	/** Set the over scroll listener **/
	public void setOnOverScrollListener(OnOverScrollListener overscrollListener) {
		this.overScrollListener = overscrollListener;
	}
	
	/** Call by the HorizontalScrollView when over scroll occurs **/
	private void onOverScrollXBy(int dx, int scrollX, int scrollY,
			int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		if(overScrollListener != null)
			overScrollListener.onOverScrollBy(dx, 0, scrollX, scrollY,
					scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	
	/** Call by the VerticalScrollView when over scroll occurs **/
	private void onOverScrollYBy(int dy, int scrollX, int scrollY,
			int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		if(overScrollListener != null)
			overScrollListener.onOverScrollBy(0, dy, scrollX, scrollY,
					scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	
	/** An interface that gives an access to the over scroll motion **/
	public interface OnOverScrollListener {
		/**
		 * @param deltaX Change in X in pixels
		 * @param deltaY Change in Y in pixels
		 * @param scrollX Current X scroll value in pixels before applying deltaX
		 * @param scrollY Current Y scroll value in pixels before applying deltaY
		 * @param scrollRangeX Maximum content scroll range along the X axis
		 * @param scrollRangeY Maximum content scroll range along the Y axis
		 * @param maxOverScrollX Number of pixels to overscroll by in either direction along the X axis.
		 * @param maxOverScrollY Number of pixels to overscroll by in either direction along the Y axis.
		 * @param isTouchEvent true if this scroll operation is the result of a touch event
		 */
		public void onOverScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
	}
	
	
	/**** Event handling ****/
	
	/** We intercept all the event. We will dispatch them ourselves **/
	@Override
 	public boolean onInterceptTouchEvent(MotionEvent ev) {
		super.onInterceptTouchEvent(ev);
		return true; 
	}
	
	/** We dispatch the event to the both VerticalScrollView and HorizontalScrollView **/
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		MotionEvent e = MotionEvent.obtain(ev);
		boolean rv = mVerticalScrollView.dispatchTouchEvent(e);
		e.recycle();
		
		e = MotionEvent.obtain(ev);
		// Adapt the event location according to the scroll position the parent vertical scrollView
		e.setLocation(e.getX(), e.getY()+mVerticalScrollView.getScrollY());
		boolean rh = mHorizontalScrollView.dispatchTouchEvent(e);
		e.recycle();
		
		return rh || rv;
	}
	
	/** This boolean marks the request of the cancelation of the child's
	 * view event handling. Asked by the VerticalScrollView which don't
	 * have access of the child view. */
	private boolean cancelRequest = false;

	/** The personnalized vertical scrollView. Here, we just need to ask
	 * for child's event handling cancelation when entering to the scroll mode */
	private class MyVerticalScrollView extends ScrollView {
		public MyVerticalScrollView(Context context) {
			super(context);
		}
		
		/** The scroll view intercept the events when they are intented to do scroll motion **/
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			boolean r = super.onInterceptTouchEvent(ev);
			// Going to scroll mode, request child's event handling cancelation
			if(r) cancelRequest = true;
			return r;
		}
		
		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			super.onScrollChanged(l, t, oldl, oldt);
			IXYScrollView.this.onScrollYChanged(t);
		}
		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			IXYScrollView.this.onOverScrollYBy(deltaY, scrollX, scrollY,
					scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
					
			return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
		}
	}
	
	/** This personnalized frameView will just stop the propagations of the events **/
	private class MyFrameLayoutIntermediate extends FrameLayout {
		public MyFrameLayoutIntermediate(Context context) {
			super(context);
		}

		/** We always intercept the event **/
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			return true;
		}

		/** We override this method to do not let the super method propagate the event **/
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			return true;
		}
	}
	
	/** The personnalized HorizontalScrollView. Here, we apply the cancelation request **/
	private class MyHorizontalScrollView extends HorizontalScrollView {
		public MyHorizontalScrollView(Context context) {
			super(context);
		}
		
		/** We let the super method intercept the event if needed. We also intercept
		 * the event if the vertical scroll view has sent the cancelation request **/
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			boolean r = super.onInterceptTouchEvent(ev) || cancelRequest;
			cancelRequest = false;
			return r;
		}
		
		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			super.onScrollChanged(l, t, oldl, oldt);
			IXYScrollView.this.onScrollXChanged(l);
		}
		
		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			IXYScrollView.this.onOverScrollXBy(deltaX, scrollX, scrollY,
					scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
					
			return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
		}
	}
}
