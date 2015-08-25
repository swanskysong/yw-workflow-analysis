package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;

import org.jooq.Field;

@SuppressWarnings("rawtypes")
public class Column {

    public static Field BEGIN_ID        = field("begin_id");
    public static Field COMMENT_ID      = field("comment_id");
    public static Field CONTAINER_BLOCK = field("container_block");
    public static Field DESCRIPTION     = field("description");
    public static Field END_ID          = field("end_id");
    public static Field ID              = field("id");
    public static Field IS_WORKFLOW     = field("is_workflow");
    public static Field IS_FUNCTION     = field("is_function");
    public static Field KEYWORD         = field("keyword");
    public static Field LINE_TEXT       = field("line_text");
    public static Field LINE_NUMBER     = field("line_number");
    public static Field NAME            = field("name");
    public static Field PATH            = field("path");
    public static Field PROGRAM_ID      = field("program_id");
    public static Field QUALIFIED_NAME  = field("qualified_name");
    public static Field QUALIFIES       = field("qualifies");
    public static Field RANK_ON_LINE    = field("rank_on_line");
    public static Field RANK_IN_COMMENT = field("rank_in_comment");
    public static Field SOURCE_ID       = field("source_id");
    public static Field TAG             = field("tag");
    public static Field TEXT            = field("text");
    public static Field VALUE           = field("value");
    
    public static class SOURCE {
        public static Field ID              = field("source.id");
        public static Field PATH            = field("source.path");
    }

    public static class SOURCE_LINE {
        public static Field ID              = field("source_line.id");
        public static Field SOURCE_ID       = field("source_line.source_id");
        public static Field LINE_NUMBER     = field("source_line.line_number");
        public static Field LINE_TEXT       = field("source_line.line_text");
    }

    public static class COMMENT {
        public static Field ID              = field("comment.id");
        public static Field SOURCE_ID       = field("comment.source_id");
        public static Field LINE_NUMBER     = field("comment.line_number");
        public static Field RANK_ON_LINE    = field("comment.rank_on_line");
        public static Field TEXT            = field("comment.text");
    }
    
    public static class ANNOTATION {
        public static Field ID              = field("annotation.id");
        public static Field QUALIFIES       = field("annotation.qualifies");
        public static Field COMMENT_ID      = field("annotation.comment_id");
        public static Field RANK_IN_COMMENT = field("annotation.rank_in_comment");
        public static Field TAG             = field("annotation.tag");
        public static Field KEYWORD         = field("annotation.keyword");
        public static Field VALUE           = field("annotation.value");
        public static Field DESCRIPTION     = field("annotation.description");
    }

    public static class PROGRAM_BLOCK {
        public static Field CONTAINER_BLOCK = field("container_block");
        public static Field ID              = field("program_block.id");
        public static Field PARENT_ID       = field("program_block.parent_id");
        public static Field BEGIN_ID        = field("program_block.begin_id");
        public static Field END_ID          = field("program_block.end_id");
        public static Field NAME            = field("program_block.name");
        public static Field QUALIFIED_NAME  = field("program_block.qualified_name");
        public static Field IS_WORKFLOW     = field("program_block.is_workflow");
        public static Field IS_FUNCTION     = field("program_block.is_function");
    }
}
