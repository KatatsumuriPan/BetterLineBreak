package kpan.b_line_break.config.core.properties;

import com.google.common.base.Splitter;
import kpan.b_line_break.ModReference;
import kpan.b_line_break.config.core.CommentLocalizer;
import kpan.b_line_break.config.core.IConfigElement;
import kpan.b_line_break.config.core.ModConfigCategory;

import java.io.BufferedWriter;
import java.io.IOException;

public abstract class AbstractConfigProperty implements IConfigElement {

    private final String id;
    private final String commentForFile;
    private final int order;

    private boolean isReadValue = false;
    protected boolean requiresWorldRestart = false;
    protected boolean showInGui = true;
    protected boolean requiresMcRestart = false;
    protected boolean dirty = false;

    protected AbstractConfigProperty(String id, String commentForFile, int order) {
        this.id = id;
        this.commentForFile = commentForFile;
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public String getId() {
        return id;
    }

    public String getCommentForFile() {
        return commentForFile;
    }
    public String getNameTranslationKey(String path) {
        return ModReference.MODID + ".config." + path + "." + getId();
    }

    public String getCommentTranslationKey(String path) {
        return ModReference.MODID + ".config." + path + "." + getId() + ".tooltip";
    }

    public void resetDirty() {
        dirty = false;
    }

    public abstract boolean readValue(String value);

    public abstract String getAdditionalComment();

    public abstract String getTypeString();

    public abstract String getValueString();

    public abstract String getDefaultValueString();

    public abstract boolean isDefault();

    public abstract void setToDefault();

    public boolean requiresWorldRestart() {
        return requiresWorldRestart;
    }

    public boolean requiresMcRestart() {
        return requiresMcRestart;
    }

    @Override
    public boolean showInGui() {
        return showInGui;
    }

    public abstract boolean isValidValue(String str);

    @Override
    public void write(BufferedWriter out, int indent, String path) throws IOException {
        String pad0 = ModConfigCategory.getIndent(indent);

        String comment = CommentLocalizer.tryLocalize(getCommentTranslationKey(path), getCommentForFile());
        if (!comment.isEmpty() || !getAdditionalComment().isEmpty()) {

            Splitter splitter = Splitter.onPattern("\r?\n");
            if (!comment.isEmpty()) {
                for (String commentLine : splitter.split(comment)) {
                    ModConfigCategory.writeLine(out, pad0, "# ", commentLine);
                }
            }
            if (!getAdditionalComment().isEmpty()) {
                for (String commentLine : splitter.split(getAdditionalComment())) {
                    ModConfigCategory.writeLine(out, pad0, "# ", commentLine);
                }
            }
        }

        String id = getId();
        if (!ModConfigCategory.allowedProperties.matchesAllOf(id)) {
            id = '"' + id + '"';
        }

        if (this instanceof AbstractConfigPropertyList list) {
            String pad1 = ModConfigCategory.getIndent(indent + 1);
            ModConfigCategory.writeLine(out, pad0, getTypeString(), ":", id, " <");

            for (String line : list.getStringValues()) {
                ModConfigCategory.writeLine(out, pad1, line);
            }

            ModConfigCategory.writeLine(out, pad0, " >");
        } else {
            ModConfigCategory.writeLine(out, pad0, getTypeString(), ":", id, "=", getValueString());
        }
    }

    public static abstract class AbstractConfigPropertyList extends AbstractConfigProperty {

        protected boolean isListLengthFixed = false;
        protected int maxListLength = -1;

        protected AbstractConfigPropertyList(String id, String commentForFile, int order) {
            super(id, commentForFile, order);
        }

        public abstract String[] getStringValues();
    }

}
