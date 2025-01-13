package sap.ass02.domain;

/**
 * A 2D vector.
 */
public final class V2d implements java.io.Serializable {
        private double x;
        private double y;
        
        /**
         * Constructor.
         * @param x the x-coordinate
         * @param y the y-coordinate
         */
        public V2d(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double x() {
            return this.x;
        }
        
        public double y() {
            return this.y;
        }
        
        /**
         * Adds the given vector to this one.
         * @param v the vector to be added
         * @return the sum of this vector with the given one
         */
        public V2d sum(V2d v) {
            return new V2d(this.x + v.x, this.y + v.y);
        }
        
        /**
         * Rotates this vector by the given degree.
         * @param degree the degree to rotate
         * @return the rotation of this vector by the given degree
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
         * Returns the module of this vector.
         * @return the module of this vector
         */
        public double abs() {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        }
        
        /**
         * Returns the normalized vector.
         * @return the normalized vector
         */
        public V2d getNormalized() {
            double module = Math.sqrt(this.x * this.x + this.y * this.y);
            return new V2d(this.x / module, this.y / module);
        }
        
        /**
         * Multiplies this vector by the given factor.
         * @param fact the factor to multiply
         * @return the multiplication of this vector by the given factor
         */
        public V2d mul(double fact) {
            return new V2d(this.x * fact, this.y * fact);
        }
        
        /**
         * Returns the string representation of this vector.
         * @return the string representation of this vector
         */
        public String toString() {
            return "V2dDTO(" + this.x + "," + this.y + ")";
        }
        
        /**
         * Returns the x-coordinate.
         * @return the x-coordinate
         */
        public double getX() {
            return this.x;
        }
        
        /**
         * Sets the x-coordinate.
         * @param x the x-coordinate
         */
        public void setX(double x) {
            this.x = x;
        }
        
        /**
         * Returns the y-coordinate.
         * @return the y-coordinate
         */
        public double getY() {
            return this.y;
        }
        
        /**
         * Sets the y-coordinate.
         * @param y the y-coordinate
         */
        public void setY(double y) {
            this.y = y;
        }
    }
