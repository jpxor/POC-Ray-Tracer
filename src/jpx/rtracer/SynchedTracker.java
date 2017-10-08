package jpx.rtracer;

import java.util.function.BiPredicate;

public class SynchedTracker<TrackedType,TestType> {
	
	private BiPredicate<TestType,TestType> predicate;
	private TestType currentValue;
	private TrackedType object = null;
	
	public SynchedTracker(BiPredicate<TestType,TestType> predicate, TestType initialValue) {
		this.currentValue = initialValue;
		this.predicate = predicate;
	}

	public synchronized boolean offer(TrackedType trackedObject, TestType testValue) {
		boolean result = predicate.test(testValue, currentValue);
		if( result ) {
			this.currentValue = testValue;
			this.object = trackedObject;
		}
		return result;
	}
	
	public TrackedType get() {
		return object;
	}

	public void setDefault(TrackedType object) { 
		this.object = object;
	}
	
}
