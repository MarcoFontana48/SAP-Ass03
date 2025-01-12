package sap.ass02.domain;

/**
 * Represents a 2D vector
 */
public class V2d implements java.io.Serializable {
        private double x;
        private double y;
        
        /**
         * Constructor
         * @param x
         * @param y
         */
        public V2d(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        /**
         * Get the x coordinate
         * @return the x coordinate
         */
        public double x() {
            return this.x;
        }
        
        /**
         * Get the y coordinate
         * @return the y coordinate
         */
        public double y() {
            return this.y;
        }
        
        /**
         * Sum of two vectors
         * @param v the other vector
         * @return the sum
         */
        public V2d sum(V2d v) {
            return new V2d(this.x + v.x, this.y + v.y);
        }
        
        /**
         * Rotation of the vector
         * @param degree the degree of rotation
         * @return the rotated vector
         */
        public V2d rotate(double degree) {
            var rad = degree * Math.PI / 180;
            var cs = Math.cos(rad);
            var sn = Math.sin(rad);
            var x1 = this.x * cs - this.y * sn;
            var y1 = this.x * sn + this.y * cs;
            var v = new V2d(x1, y1).getNormalized();
            return v;
        }
    
        /**
         * Absolute value of the vector
         * @return the absolute value
         */
        public double abs() {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        }
        
        /**
         * Normalized vector
         * @return the normalized vector
         */
        public V2d getNormalized() {
            double module = Math.sqrt(this.x * this.x + this.y * this.y);
            return new V2d(this.x / module, this.y / module);
        }
        
        /**
         * Multiplication of the vector by a factor
         * @param fact the factor
         * @return the multiplied vector
         */
        public V2d mul(double fact) {
            return new V2d(this.x * fact, this.y * fact);
        }
        
        /**
         * Converts the vector to a string
         */
        public String toString() {
            return "V2dDTO(" + this.x + "," + this.y + ")";
        }
        
        /**
         * Get the x coordinate
         * @return the x coordinate
         */
        public double getX() {
            return this.x;
        }
        
        /**
         * Set the x coordinate
         * @param x the x coordinate
         */
        public void setX(double x) {
            this.x = x;
        }
        
        /**
         * Get the y coordinate
         * @return the y coordinate
         */
        public double getY() {
            return this.y;
        }
        
        /**
         * Set the y coordinate
         * @param y the y coordinate
         */
        public void setY(double y) {
            this.y = y;
        }
    }
