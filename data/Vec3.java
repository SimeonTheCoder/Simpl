package data;

public class Vec3 {
    public double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3() {

    }

    public Vec3 add(Vec3 vec) {
        Vec3 res = new Vec3();

        res.x += this.x + vec.x;
        res.y += this.y + vec.y;
        res.z += this.z + vec.z;

        return res;
    }

    public Vec3 sub(Vec3 vec) {
        Vec3 res = new Vec3();

        res.x += this.x - vec.x;
        res.y += this.y - vec.y;
        res.z += this.z - vec.z;

        return res;
    }

    public Vec3 mul(Vec3 vec) {
        Vec3 res = new Vec3();

        res.x += this.x * vec.x;
        res.y += this.y * vec.y;
        res.z += this.z * vec.z;

        return res;
    }

    public double len() {
        return Math.sqrt(
                this.x * this.x +
                this.y * this.y +
                this.z * this.z
        );
    }
}
