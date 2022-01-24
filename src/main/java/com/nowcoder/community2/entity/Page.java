package com.nowcoder.community2.entity;

public class Page {
    //    当前页，默认为1
    private int current = 1;
    //    每页的帖子数量，默认为10
    private int limit = 10;
    //    帖子总数，根据这个计算总页数
    private int rows;
    //    页面访问路径，用来复用
    private String path;

    public Page() {
    }

    public Page(int current, int limit, int rows, String path) {
        this.current = current;
        this.limit = limit;
        this.rows = rows;
        this.path = path;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
//        每页显示的帖子数量保证不能太多，否则浏览器承载不了
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 求出当前页的起始位置数据，我们才能查出来这一页的数据
     * current * limit - limit 就是当前页第一个数据位置
     *
     * @return
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 分页功能需要跳转到最后一页，所以要获取总页码
     *
     * @return
     */
    public int getTotal() {
//        有余数就+1页
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 分页栏需要使用，当前页前两页的位置，注意边界判断
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 分页栏需要使用，后两页的位置，注意边界判断
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }
}
