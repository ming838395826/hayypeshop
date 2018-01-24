package com.happyshop.common;

/**
 * Created by Administrator on 2018/1/24.
 */

import java.io.Serializable;
import java.util.List;

/**
 *
 id：节点的 id，它对于加载远程数据很重要。
 text：要显示的节点文本。
 state：节点状态，'open' 或 'closed'，默认是 'open'。当设置为 'closed' 时，该节点有子节点，并且将从远程站点加载它们。
 checked：指示节点是否被选中。
 attributes：给一个节点添加的自定义属性。
 children：定义了一些子节点的节点数组。
 */
public class EasyUiTreeResponse implements Serializable {

    private long id;
    private String text;
    private String state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
