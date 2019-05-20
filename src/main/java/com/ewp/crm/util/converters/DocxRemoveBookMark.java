package com.ewp.crm.util.converters;

import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.finders.RangeFinder;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkup;
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

        forCTMarkup(rt.getStarts());
        forCTMarkup(rt.getEnds());
    }

    private static void forCTMarkup (List<? extends CTMarkup> ctMarkupList){
        for (CTMarkup ctMarkup : ctMarkupList) {
            try {
                List<Object> theList = null;
                if (ctMarkup.getParent() instanceof List) {
                    theList = (List) ctMarkup.getParent();
                } else {
                    theList = ((ContentAccessor) (ctMarkup.getParent())).getContent();
                }
                Object deleteMe = null;
                for (Object ox : theList) {
                    if (XmlUtils.unwrap(ox).equals(ctMarkup)) {
                        deleteMe = ox;
                        break;
                    }
                }
                if (deleteMe != null) {
                    theList.remove(deleteMe);
                }
            } catch (ClassCastException cce) {
                log.info(ctMarkup.getParent().getClass().getName());
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
