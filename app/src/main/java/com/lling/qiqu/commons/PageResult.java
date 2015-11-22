package com.lling.qiqu.commons;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

	private static final long serialVersionUID = -8689423380671812808L;

	public static final int DEFAULT_PAGE_SIZE = 10;
	
	private int pageIndex;
	private int pageSize;
	private int totalCount;
	private int pageCount;
	private List<T> list;

	public PageResult() {
		pageIndex = 1;
		pageSize = DEFAULT_PAGE_SIZE;
	}

	public PageResult(String _pageIndex) {
		this(_pageIndex, "");
	}

	public PageResult(String _pageIndex, String _pageSize) {
		int pageIndex = 1;
		int pageSize = DEFAULT_PAGE_SIZE;
		if (null == _pageIndex || "".equals(_pageIndex)) {
			pageIndex = 1;
		} else {
			try {
				pageIndex = Integer.parseInt(_pageIndex);
			} catch (Exception e) {
				pageIndex = 1;
			}
		}

		if (null == _pageSize || "".equals(_pageSize)) {
			pageSize = DEFAULT_PAGE_SIZE;
		} else {
			try {
				pageSize = Integer.parseInt(_pageSize);
			} catch (Exception e) {
				pageSize = DEFAULT_PAGE_SIZE;
			}
		}
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;

	}

	public PageResult(int totalCount, Limit limit, List<T> list) {
		this.list = list;
        if(null == limit){
        	return;
        }
		this.pageIndex = limit.getPageId();
		this.pageSize = limit.getSize();
		this.setTotalCount(totalCount);
	}

	public PageResult(int pageIndex, int pageSize) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;

	}

	/**
	 * @return the list
	 */
	public List<T> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<T> list) {
		this.list = list;
	}

	public PageResult(int pageIndex) {
		this(pageIndex, DEFAULT_PAGE_SIZE);
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getFirstResult() {
		return (pageIndex - 1) * pageSize;
	}

	public boolean getHasPrevious() {
		return pageIndex > 1;
	}

	public boolean getHasNext() {
		return pageIndex < pageCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		pageCount = totalCount / pageSize + (totalCount % pageSize == 0 ? 0 : 1);
		if (0 == totalCount) {
			if (pageIndex != 1) {
				pageIndex = 1;
			}
		} else {
			if (pageIndex > pageCount) {
				pageIndex = pageCount;
			}
		}
	}

	public int getPrev() {
		return this.pageIndex - 1;
	}

	public int getNext() {
		return this.pageIndex + 1;
	}

	public boolean isEmpty() {
		return 0 == totalCount;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageCount(int countInPage) {
		this.pageCount = countInPage;
	}

}