package cn.gudqs7.plugins.docer.annotation;

import cn.gudqs7.plugins.docer.enums.CommentTagEnum;
import cn.gudqs7.plugins.docer.pojo.annotation.CommentInfoTag;

/**
 * @author wq
 * @date 2022/5/29
 */
public abstract class AbstractFieldAnnotationHolder extends AbstractAnnotationHolder {

    protected void setCommentInfoByTag(CommentInfoTag commentInfoTag, String tagName, String tagVal) {
        switch (CommentTagEnum.of(tagName)) {
            case REQUIRED:
                commentInfoTag.setRequired(getBooleanVal(tagVal));
                break;
            case HIDDEN:
                commentInfoTag.setHidden(getBooleanVal(tagVal));
                break;
            case IMPORTANT:
                commentInfoTag.setImportant(getBooleanVal(tagVal));
                break;
            case EXAMPLE:
                commentInfoTag.setExample(tagVal);
                break;
            case NOTES:
                commentInfoTag.appendNotes(tagVal);
                break;
            default:
                break;
        }
    }

}
