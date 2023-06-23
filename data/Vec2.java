package data;

public class Vec2 {
    public double x, y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2() {

    }

    public Vec2 add(Vec2 vec) {
        Vec2 res = new Vec2();

        res.x = this.x + vec.x;
        res.y = this.y + vec.y;

        return res;
    }

    public Vec2 sub(Vec2 vec) {
        Vec2 res = new Vec2();

        res.x = this.x - vec.x;
        res.y = this.y - vec.y;

        return res;
    }

    public Vec2 mul(Vec2 vec) {
        Vec2 res = new Vec2();

        res.x = this.x * vec.x;
        res.y = this.y * vec.y;

        return res;
    }

    public Vec2 div(Vec2 vec) {
        Vec2 res = new Vec2();

        res.x = this.x / vec.x;
        res.y = this.y / vec.y;

        return res;
    }

    public double len() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vec2 pow(double power) {
        Vec2 res = new Vec2();

        res.x += Math.pow(this.x, power);
        res.y += Math.pow(this.y, power);

        return res;
    }
}
