package com.example.asmuniz.trojanow.util;

import com.example.asmuniz.trojanow.obj.Feed;
import com.example.asmuniz.trojanow.obj.Post;
import com.example.asmuniz.trojanow.obj.User;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Contains common operations that will be perform regarding Posts.
 */
public class PostCenter {

    // enum that allows users to post anonymously
    enum PostAs {
        DEFAULT(""),
        ANONYMOUS("anonymous");

        private String name;

        PostAs(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // This class is not meant to be subclassed
    private PostCenter() {}

    public static String formatMessage(String text, SensorCenter.Data type) {
        return text + " " + SensorCenter.printData(type);
    }

}
