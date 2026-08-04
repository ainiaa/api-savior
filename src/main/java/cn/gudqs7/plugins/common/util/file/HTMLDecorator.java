package cn.gudqs7.plugins.common.util.file;

import com.intellij.openapi.diagnostic.Logger;
import com.youbenzi.mdtool.export.Decorator;
import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.MDToken;
import com.youbenzi.mdtool.markdown.TableCellAlign;
import com.youbenzi.mdtool.markdown.bean.Block;
import com.youbenzi.mdtool.markdown.bean.ValuePart;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * 在原来的基础上给 H1 添加了 id
 *
 * @author WQ
 */
public class HTMLDecorator implements Decorator {

    private static final Logger LOG = Logger.getInstance(HTMLDecorator.class);

    public final String idPrefix;

    private StringBuilder content = new StringBuilder();

    public HTMLDecorator(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    @Override
    public void beginWork(String outputFilePath) {

    }

    @Override
    public void decorate(List<Block> list) {
        for (Block block : list) {
            try {
                String str;
                switch (block.getType()) {
                    case CODE:
                        str = codeParagraph(block.getValueParts());
                        break;
                    case HEADLINE:
                        str = headerParagraph(block.getValueParts(), block.getLevel());
                        break;
                    case QUOTE:
                        str = quoteParagraph(block.getListData());
                        break;
                    case TABLE:
                        str = tableParagraph(block.getTableData());
                        break;
                    case LIST:
                        str = listParagraph(block.getListData());
                        break;
                    default:
                        str = commonTextParagraph(block.getValueParts(), true);
                        break;
                }

                content.append(str).append("\n");
            } catch (Exception e) {
                LOG.warn("Failed to render Markdown block", e);
            }
        }
    }

    @Override
    public void afterWork(String outputFilePath) {
        try {
            Files.write(Path.of(outputFilePath), content.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.warn("Failed to write HTML file", e);
        }
    }

    public String outputHtml() {
        return content.toString();
    }

    private String codeParagraph(ValuePart[] valueParts) {

        String value = valueParts[0].getValue();
        StringBuilder tmp = new StringBuilder("<pre>");
        tmp.append("<code class=\"language-json\">");
        value = escapeHtml(value);
        if (value.endsWith("\n")) {
            value = value.substring(0, value.length() - "\n".length());
        }
        tmp.append(value);
        tmp.append("</code>");
        tmp.append("</pre>\n");

        return tmp.toString();
    }

    private String headerParagraph(ValuePart[] valueParts, int level) {
        if (level == 1) {
            String id = "";
            if (ArrayUtils.isNotEmpty(valueParts)) {
                id = idPrefix + valueParts[0].getValue();
            }
            return oneLineHtml(valueParts, "h" + level, "id=\"" + escapeHtml(id) + "\"");
        } else {
            return oneLineHtml(valueParts, "h" + level);
        }
    }

    private String listParagraph(List<Block> listData) {
        StringBuilder tmp = new StringBuilder();
        for (Block block : listData) {
            tmp.append(listParagraph(block.getType(), block.getListData()));
        }
        return tmp.toString();
    }

    private String listParagraph(BlockType blockType, List<Block> listData) {
        StringBuilder tmp = new StringBuilder();

        switch (blockType) {
            case ORDERED_LIST:
                tmp.append(orderedListParagraph(listData)).append("\n");
                break;
            case UNORDERED_LIST:
                tmp.append(unorderedListParagraph(listData)).append("\n");
                break;
            case QUOTE:
                tmp.append(quoteParagraph(listData)).append("\n");
                break;
            case TODO_LIST:
                tmp.append(todoListParagraph(listData)).append("\n");
                break;
            default:
                break;
        }
        return tmp.toString();
    }

    private String formatByType(BlockType type, String value, ValuePart valuePart) {
        switch (type) {
            case BOLD_WORD:
                return "<strong>" + value + "</strong>";
            case ITALIC_WORD:
                return "<em>" + value + "</em>";
            case STRIKE_WORD:
                return "<del>" + value + "</del>";
            case CODE_WORD:
                return "<code>" + value + "</code>";
            case HEADLINE:
                int level = valuePart.getLevel() + 1;
                return "<h" + level + ">" + value + "</h" + level + ">";
            case LINK:
                return "<a href=\"" + sanitizeUrl(valuePart.getUrl()) + "\" title=\"" + value
                        + "\">" + value + "</a>";
            case IMG:
                return "<img src=\"" + sanitizeUrl(valuePart.getUrl()) + "\" title=\"" + escapeHtml(valuePart.getTitle())
                        + "\" alt=\"" + escapeHtml(valuePart.getTitle()) + "\" />";
            case ROW:
                return "<br/>";
            default:
                return value;
        }
    }

    private String tableParagraph(List<List<Block>> tableData) {

        int nRows = tableData.size();
        int nCols = 0;
        for (List<Block> list : tableData) {
            int s = list.size();
            if (nCols < s) {
                nCols = s;
            }
        }
        StringBuilder tmp = new StringBuilder("<table>\n");

        for (int i = 0; i < nRows; i++) {
            tmp.append("<tr>\n");
            List<Block> colDatas = tableData.get(i);
            for (int j = 0; j < nCols; j++) {
                boolean head = (i == 0);
                if (j >= colDatas.size()) {
                    tmp.append(buildColBegin(head, TableCellAlign.NONE));
                    tmp.append("</").append(head ? "th" : "td").append(">\n");
                    continue;
                }
                Block block = colDatas.get(j);
                tmp.append(buildColBegin(head, block.getAlign()));
                try {
                    tmp.append(commonTextParagraph(colDatas.get(j).getValueParts(), false));
                } catch (Exception e) {
                    tmp.append("");
                }
                tmp.append("</" + (head ? "th" : "td") + ">\n");
            }
            tmp.append("</tr>\n");
        }
        tmp.append("</table>\n");
        return tmp.toString();
    }

    private String buildColBegin(boolean head, TableCellAlign align) {
        String alignString = "";
        if (align != TableCellAlign.NONE) {
            alignString = "align=\"" + align.value() + "\"";
        }
        return "<" + (head ? "th" : "td") + " " + alignString + ">";
    }

    private String abstractListParagraph(List<Block> listData, LineHelper lineHelper) {
        StringBuilder tmp = new StringBuilder(lineHelper.parentTagBegin() + "\n");
        for (final Block block : listData) {
            Block lineBlock = block.getLineData();
            String content;
            if (lineBlock.getType() == BlockType.HEADLINE) {
                content = headerParagraph(lineBlock.getValueParts(), lineBlock.getLevel());
            } else {
                content = commonTextParagraph(lineBlock.getValueParts(), lineHelper.needDefaultChild());
            }
            tmp.append(lineHelper.childTagBegin(block));
            tmp.append(content).append("\n").append(lineHelper.subList(block));
            tmp.append(lineHelper.childTagEnd());
        }
        tmp.append(lineHelper.parentTagEnd() + "\n");

        return tmp.toString();
    }

    private String quoteParagraph(List<Block> listData) {
        return abstractListParagraph(listData, new DefaultLineHelper("blockquote"));
    }

    private String unorderedListParagraph(List<Block> listData) {
        return abstractListParagraph(listData, new DefaultLineHelper("ul", "li"));
    }

    private String orderedListParagraph(List<Block> listData) {
        return abstractListParagraph(listData, new DefaultLineHelper("ol", "li"));
    }

    private String todoListParagraph(List<Block> listData) {
        return abstractListParagraph(listData, new DefaultLineHelper("ul", "li") {
            @Override
            public String decorate(Block block, String tag) {
                if (block.getMdToken().equals(MDToken.TODO_LIST_UNCHECKED)) {
                    return "<" + tag + "><i class=\"unchecked_icon\"></i>";
                } else {
                    return "<" + tag + "><i class=\"checked_icon\"></i>";
                }
            }
        });
    }

    private String commonTextParagraph(ValuePart[] valueParts, boolean needPTag) {
        return oneLineHtml(valueParts, needPTag ? "p" : null);
    }

    private String oneLineHtml(ValuePart[] valueParts, String tagName) {
        return oneLineHtml(valueParts, tagName, "");
    }

    private String oneLineHtml(ValuePart[] valueParts, String tagName, String attr) {
        StringBuilder result = new StringBuilder();
        if (tagName != null && !tagName.trim().equals("")) {
            result.append("<" + tagName + " " + attr + ">");
        }
        for (ValuePart valuePart : valueParts) {
            BlockType[] types = valuePart.getTypes();

            String value = valuePart.getValue();
            if (hasLink(types)) {
                value = valuePart.getTitle();
            }
            value = escapeHtml(value);
            if (types != null) {
                for (BlockType type : types) {
                    value = formatByType(type, value, valuePart);
                }
            }
            result.append(value);
        }
        if (tagName != null && !tagName.trim().equals("")) {
            result.append("</" + tagName + ">");
        }
        return result.toString();
    }

    private boolean hasLink(BlockType[] types) {
        if (types == null) {
            return false;
        }
        for (BlockType blockType : types) {
            if (blockType == BlockType.LINK) {
                return true;
            }
        }
        return false;
    }

    static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    static String sanitizeUrl(String url) {
        if (url == null) {
            return "";
        }
        String normalizedUrl = url.trim();
        int colon = normalizedUrl.indexOf(':');
        if (colon > 0) {
            String scheme = normalizedUrl.substring(0, colon).toLowerCase(Locale.ROOT);
            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                return "";
            }
        }
        return escapeHtml(normalizedUrl);
    }

    public abstract class LineHelper {
        public abstract String subList(Block block);

        public abstract String parentTagBegin();

        public abstract String parentTagEnd();

        public abstract String childTagBegin(Block block);

        public abstract String childTagEnd();

        public abstract boolean needDefaultChild();

        public String decorate(Block block, String tag) {
            if (tag == null) {
                return "";
            }
            return "<" + tag + ">";
        }
    }

    public class DefaultLineHelper extends LineHelper {
        private String parentTag;
        private String childTag;

        public DefaultLineHelper(String parentTag, String childTag) {
            this.parentTag = parentTag;
            this.childTag = childTag;
        }

        public DefaultLineHelper(String parentTag) {
            this.parentTag = parentTag;
            this.childTag = null;
        }

        public boolean needDefaultChild() {
            return childTag == null;
        }

        public String parentTagBegin() {
            return "<" + parentTag + ">";
        }

        public String parentTagEnd() {
            return "</" + parentTag + ">";
        }

        public String childTagBegin(Block block) {
            return decorate(block, childTag);
        }

        public String childTagEnd() {
            if (childTag == null) {
                return "";
            }
            return "</" + childTag + ">";
        }

        public String subList(Block block) {
            if (block.getType() != null) {
                return listParagraph(block.getType(), block.getListData());
            } else {
                return "";
            }
        }
    }
}
