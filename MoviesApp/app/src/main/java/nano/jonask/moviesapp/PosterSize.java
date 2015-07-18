package nano.jonask.moviesapp;

/**
 * Created by Jonas Kirkemyr on 16.07.2015.
 */

/**
 * The different postersizes which can be retrieved
 */
public enum PosterSize {
    W92 {
        @Override
        public String toString() {
            return "w92";
        }
    },
    W154 {
        @Override
        public String toString() {
            return "w154";
        }
    },
    W185 {
        @Override
        public String toString() {
            return "w185";
        }
    },
    W342 {
        @Override
        public String toString() {
            return "w342";
        }
    },
    W500 {
        @Override
        public String toString() {
            return "w500";
        }
    },
    W780 {
        @Override
        public String toString() {
            return "w780";
        }
    },
    ORIGINAL {
        @Override
        public String toString() {
            return "original";
        }
    }

}
