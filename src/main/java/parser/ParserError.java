package parser;

public interface ParserError {

    class InvalidFormat implements ParserError {

        private String ref;
        private String expectedFormat;
        private String message;

        public InvalidFormat(String ref, String expectedFormat, String message) {
            this.ref = ref;
            this.expectedFormat = expectedFormat;
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        @Override
        public String toString() {
            return "InvalidFormat(" + ref + ", " + expectedFormat + ", " + message + ')';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InvalidFormat that = (InvalidFormat) o;

            if (ref != null ? !ref.equals(that.ref) : that.ref != null) return false;
            if (expectedFormat != null ? !expectedFormat.equals(that.expectedFormat) : that.expectedFormat != null)
                return false;
            return message != null ? message.equals(that.message) : that.message == null;
        }
    }

    class MissingName implements ParserError {

        private String name;

        public MissingName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "MissingName(" + name + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MissingName that = (MissingName) o;

            return name != null ? name.equals(that.name) : that.name == null;
        }
    }

    class MissingCell implements ParserError {

        private String ref;

        public MissingCell(String ref) {
            this.ref = ref;
        }

        @Override
        public String toString() {
            return this.ref;
        }
    }
}
