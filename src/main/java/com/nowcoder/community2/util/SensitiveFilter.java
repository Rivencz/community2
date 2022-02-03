package com.nowcoder.community2.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //    如果出现敏感词就都使用该字符串进行代替
    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    //    在构造器执行之后就直接初始化前缀树，将类路径下的txt过滤文件全部写入到前缀树中
    @PostConstruct
    public void init() {
        try (
//                放到try的括号中，写入结束之后会自动生成finally并将流关闭
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
//        将字节流转换为字符流进行读取
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null) {
//                调用对应方法，将该字符串添加到前缀树中
                this.addKeyword(keyword);
                System.out.println(keyword);
            }
        } catch (IOException e) {
            logger.error("加载文件失败！" + e.getMessage());
        }
    }

    /**
     * 过滤敏感词，外部需要调用的方法，需要三个指针
     *
     * @param text
     * @return
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
//        指针1
        TrieNode temp = rootNode;
//        指针2，表示以begin为开头的词，循环会让begin从0到结尾
        int begin = 0;
//        指针3，表示在当前begin下，遍历到了第几个字符
        int position = 0;
//        过滤之后拼接出来的字符串
        StringBuilder res = new StringBuilder();
        while (begin < text.length()) {
//            如果当前begin情况还没有遍历到结尾
            if (position < text.length()) {
                char ch = text.charAt(position);
//                先判断是不是特殊字符，如果是就进行特判
                if (isSymbol(ch)) {
//                    如果该字符是遍历到的第一个，那么就直接加入res并让begin+1，如果是敏感词中间的字符，那么就只让position+1
                    if (temp == rootNode) {
                        begin++;
                        res.append(ch);
                    }
                    position++;
                    continue;
                }

                temp = temp.getSubNodes(ch);
//                为null，那么肯定不是敏感词
                if (temp == null) {
                    res.append(text.charAt(begin));
//                    三个指针重新归位
                    position = ++begin;
                    temp = rootNode;
                } else if (temp.isKeywordEnd()) {
//                    如果为前缀树中的敏感词，并且到了结尾，那么就将这个敏感词通过其他符号进行代替
                    res.append(REPLACEMENT);
//                    指针重新归位
                    begin = ++position;
                    temp = rootNode;
                } else {
//                    否则，说明正在寻找符合条件的过滤词，
                    position++;
                }
                System.out.println(res);
            } else {
//                遍历到结尾了，也没发现敏感词，就让begin+1从新的开头进行遍历
                res.append(text.charAt(begin));
//                不要忘记让三个指针都要重新归位
                position = ++begin;
                temp = rootNode;
            }
        }
        return res.toString();
    }

    /**
     * 判断是否为特殊字符，为true，说明是特殊字符
     *
     * @param ch
     * @return
     */
    private boolean isSymbol(Character ch) {
//        0x2E80~0x9FFF之内的是东南亚文字，在此范围之外的才表示为特殊符号
        return !CharUtils.isAsciiAlphanumeric(ch) && (ch < 0x2E80 || ch > 0x9FFF);
    }

    private void addKeyword(String keyword) {
        TrieNode temp = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char ch = keyword.charAt(i);
            TrieNode subNode = temp.getSubNodes(ch);
//            如果没有该子节点，就创建一个
            if (subNode == null) {
                subNode = new TrieNode();
                temp.addSubNode(ch, subNode);
            }
//            添加之后将当前临时节点向下移
            temp = subNode;
//            如果是最后一个节点了，将他的isKeywordEnd设置为true
            if (i == keyword.length() - 1) {
                temp.setKeywordEnd(true);
            }
        }
    }

    private class TrieNode {
        private boolean isKeywordEnd = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character c, TrieNode node) { subNodes.put(c, node); }

        public TrieNode getSubNodes(Character c) { return subNodes.get(c); }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
    }
}
