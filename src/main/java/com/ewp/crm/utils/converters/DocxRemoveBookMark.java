package com.ewp.crm.utils.converters;

import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.finders.RangeFinder;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.ContentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DocxRemoveBookMark {
    protected static Logger log = LoggerFactory.getLogger(DocxRemoveBookMark.class);

    public static void fixRange(List<Object> paragraphs, String startElement,
                                 String endElement) throws Exception {

        RangeFinder rt = new RangeFinder(startElement, endElement);
        new TraversalUtil(paragraphs, rt);

        for (CTBookmark bm : rt.getStarts()) {
            try {
                List<Object> theList = null;
                if (bm.getParent() instanceof List) {
                    theList = (List) bm.getParent();
                } else {
                    theList = ((ContentAccessor) (bm.getParent())).getContent();
                }
                Object deleteMe = null;
                for (Object ox : theList) {
                    if (XmlUtils.unwrap(ox).equals(bm)) {
                        deleteMe = ox;
                        break;
                    }
                }
                if (deleteMe != null) {
                    theList.remove(deleteMe);
                }
            } catch (ClassCastException cce) {
                log.error(cce.getMessage(), cce);
            }
        }
        for (CTMarkupRange mr : rt.getEnds()) {
            try {
                List<Object> theList = null;
                if (mr.getParent() instanceof List) {
                    theList = (List) mr.getParent();
                } else {
                    theList = ((ContentAccessor) (mr.getParent())).getContent();
                }
                Object deleteMe = null;
                for (Object ox : theList) {
                    if (XmlUtils.unwrap(ox).equals(mr)) {
                        deleteMe = ox;
                        break;
                    }
                }
                if (deleteMe != null) {
                    theList.remove(deleteMe);
                }
            } catch (ClassCastException cce) {
                log.info(mr.getParent().getClass().getName());
                log.error(cce.getMessage(), cce);
            }
        }
    }

    private static boolean remove(List<Object> theList, Object bm) {
        for (Object ox : theList) {
            if (XmlUtils.unwrap(ox).equals(bm)) {
                return theList.remove(ox);
            }
        }
        return false;
    }
}
