package sap.ass02.domain;

public class V2d implements java.io.Serializable {
        
        private double x;
        private double y;
        
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
        
        public V2d sum(V2d v) {
            return new V2d(this.x + v.x, this.y + v.y);
        }
        
        public V2d rotate(double degree) {
            var rad = degree * Math.PI / 180;
            var cs = Math.cos(rad);
            var sn = Math.sin(rad);
            var x1 = this.x * cs - this.y * sn;
            var y1 = this.x * sn + this.y * cs;
            var v = new V2d(x1, y1).getNormalized();
            return v;
        }
        
        public double abs() {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        }
        
        public V2d getNormalized() {
            double module = Math.sqrt(this.x * this.x + this.y * this.y);
            return new V2d(this.x / module, this.y / module);
        }
        
        public V2d mul(double fact) {
            return new V2d(this.x * fact, this.y * fact);
        }
        
        public String toString() {
            return "V2dDTO(" + this.x + "," + this.y + ")";
        }
        
        public double getX() {
            return this.x;
        }
        
        public void setX(double x) {
            this.x = x;
        }
        
        public double getY() {
            return this.y;
        }
        
        public void setY(double y) {
            this.y = y;
        }
    }
