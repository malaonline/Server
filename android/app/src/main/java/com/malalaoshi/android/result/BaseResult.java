package com.malalaoshi.android.result;


/**
 * 代表请求结果的基类
 */

public class BaseResult<T> {
	private int count;
	private String next;
	private String previous;
	private T results;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public T getResults() {
		return results;
	}

	public void setResults(T results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "BaseResult{" +
				", count=" + count +
				", next=" + next +
				", previous=" + previous +
				", results=" + results +
				'}';
	}

}
