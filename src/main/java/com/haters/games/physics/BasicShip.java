package com.haters.games.physics;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import com.haters.games.output.SERIALIZER_TYPE;

/**
 * Created by flvs on 8/9/15.
 */
public abstract class BasicShip {

    protected int id;
    protected Vec2 mouse_pos;

    protected Body body;
    protected SpaceWorld world;
    protected DestroyPool killthen;

    private LinkedList<Bullet> bullets = new LinkedList<Bullet>();
    private long lastFireTime = 0;
    private final static int maximumActiveBullets = 30;

    protected boolean isbot = true;
    private int currentEnergy;
    protected final long timeCreated = new Date().getTime();
    private long lasthealtime = 0;
    private long lastDamageTime = -1;
	private long damagePeriod = 100;
    

    protected DetectEntitiesCallback detectionCallback;
	private boolean readyToDestroy = false;
	private String name = "";

    protected final static float attackModeLinearDamping = 1.0f;
    protected final static float cruiseModeLinearDamping = 3.0f;

    protected BasicShip(SpaceWorld world, Vec2 pos, int id, DestroyPool killthen) {
        this.id = id;
        this.world = world;
        this.killthen = killthen;
        this.currentEnergy = this.getTotalEnergy();
        init(pos);
    }

    protected BasicShip(SpaceWorld world, int id, DestroyPool killthen, boolean isbot) {
        this.id = id;
        this.world = world;
        this.killthen = killthen;
        this.isbot = isbot;
        this.currentEnergy = this.getTotalEnergy();
        init(new Vec2(0,10));

    }

    public int getId() {
        return id;
    }

    public Body getBody() {
        return body;
    }

    public World getWorld(){
        return world.getWorld();
    }
    
    public SpaceWorld getSpaceWorld(){
        return world;
    }

    public List<Bullet> getBullets() {
        return this.bullets;
    }
    

	public boolean avoidColision() {
		float rayLength = 10;
		int[] angles = new int[10];
		for (int i=0;i<angles.length; i++) {
			angles[i] = i*360/angles.length;
		}	
		Vec2 sum = null;
		for(Fixture fix  : detectionCallback.othersFixtures){
			for(int i=0; i<angles.length;i++){
			
				float angle =  angles[i] * MathUtils.DEG2RAD;
				RayCastOutput output = new RayCastOutput();
				RayCastInput input = new RayCastInput();
				input.p1.x = this.getBody().getPosition().x;
				input.p1.y = this.getBody().getPosition().y;
				input.maxFraction = 1;
				
				Vec2 p2 = this.body.getWorldPoint( new Vec2(MathUtils.sin(angle),MathUtils.cos(angle)).mul(rayLength));
				input.p2.x = p2.x;
				input.p2.y = p2.y;
				if(fix.raycast(output, input, 1)){
					Vec2  normal = output.normal.mul(rayLength);
					if(sum == null){
						sum = new Vec2();
					}
					sum = sum.add(normal);
				}
			}
		}
		if(sum != null){
			sum.normalize();
			
			Vec2 pointToGo = this.getBody().getPosition().add(sum.mul(rayLength));
			rotateTo(pointToGo);
			return false;
		}
		return true;
	}

	
	public boolean shouldFire(Set<SpaceShip> enemies){
		for (SpaceShip enemy : enemies) {
			Vec2 vectorToEnemy = new  Vec2(enemy.getBody().getPosition().x-this.body.getPosition().x,enemy.getBody().getPosition().y- this.body.getPosition().y);
			Vec2 direction = this.body.getWorldVector(new Vec2(1, 0));
			float angle = angleBetweenToVector(vectorToEnemy,direction);
			return MathUtils.abs(angle) < 30 *MathUtils.DEG2RAD;
		}
		return false;
	}
    
	public void rotateTo(Vec2 point) {
		
		Vec2 vectorToPoint = new  Vec2(point.x-this.body.getPosition().x,point.y- this.body.getPosition().y);
			
		float angle = MathUtils.atan2(vectorToPoint.y, vectorToPoint.x);
		rotate(angle);
	}
	
	protected void rotate(float angle) {
		if (angle < 0) angle += 2 * MathUtils.PI;
		
		float desiredAngle = angle;
		//System.out.println("desiredAngle: " + desiredAngle * MathUtils.RAD2DEG + ":: angle:" + this.body.getAngle());
		
		float nextAngle = this.body.getAngle() + this.body.getAngularVelocity() / 60.0f;
		
		float totalRotation = desiredAngle - nextAngle;
		while ( totalRotation < -180 * MathUtils.DEG2RAD ) totalRotation += 360 * MathUtils.DEG2RAD;
		while ( totalRotation >  180 * MathUtils.DEG2RAD ) totalRotation -= 360 * MathUtils.DEG2RAD;
		float desiredAngularVelocity = totalRotation * 60;
		if(isbot){
			float change = 10 * MathUtils.DEG2RAD; //allow 10 degree rotation per time step
			desiredAngularVelocity = MathUtils.min( change, MathUtils.max(-change, desiredAngularVelocity));
		}
		
		float impulse = this.body.getInertia() * desiredAngularVelocity;// disregard time factor
		this.body.applyAngularImpulse(impulse);
	}


    public void fire() {

        long now = new Date().getTime();
        if( now - lastFireTime > Bullet.FireFrequency) {
            Bullet bullet = Bullet.create((SpaceShip)this,Sequence.getSequence());
            this.bullets.add(bullet);
            bullet.fire();
            lastFireTime = now;
        }
        removeExcessBullets();
    }

    protected void removeExcessBullets(){
        int wastebullets = bullets.size() - maximumActiveBullets;
        if(wastebullets > 0){
            for (int i = 0; i < wastebullets; i++) {
                Bullet b = bullets.pop();
                killthen.add(b);
            }
        }
    }

    protected float angleBetweenToVector(Vec2 v1, Vec2 v2){
        float angle = MathUtils.atan2(v2.y, v2.x) - MathUtils.atan2(v1.y, v1.x);
        if (angle < 0) angle += 2 * MathUtils.PI;
        return angle;

    }

    @Override
    public String toString() {
        return "plane : {id: "+id+"}";
    }

    public int getCurrentEnergy(){
        return this.currentEnergy;
    }

    public void damage(Bullet b) {
        this.currentEnergy -= b.getDamage();
        this.lastDamageTime = new Date().getTime();
    }

    public void destroy() {
        for (Bullet bullet : this.bullets) {
            bullet.destroyCascade();
            bullet = null;
        }
        bullets.clear();
        this.world.getWorld().destroyBody(this.body);
    }

    public Set<SpaceShip> getShipsInRange() {
        return this.detectionCallback.planes;
    }
    
    public Set<Bullet> getBulletsInRange() {
        return this.detectionCallback.bullets;
    }
    
    public Set<Energy> getGarbagesInRange(){
        return this.detectionCallback.energies;
    }
    
    public Set<SpaceShip> getAlivePlayersInRange(){
    	Set<SpaceShip> players = getShipsInRange();
    	Set<SpaceShip> alive = new LinkedHashSet<SpaceShip>();
		for (SpaceShip spaceShip : players) {
			if(spaceShip.getType() != SERIALIZER_TYPE.DEAD && !spaceShip.isbot()){
				alive.add(spaceShip);
			}
		}
		return alive;		
    }

    public Boundaries getBoundsInRange() {
        return this.detectionCallback.boundaries;
    }

    public void detectGameEntities() {
        detectionCallback.reset();
        AABB aabb = new AABB();
        aabb.lowerBound.set(new Vec2(this.body.getPosition().x - getEnemyDetectRange(),this.body.getPosition().y - getEnemyDetectRange()));
        aabb.upperBound.set(new Vec2(this.body.getPosition().x + getEnemyDetectRange(),this.body.getPosition().y + getEnemyDetectRange()));
        this.world.getWorld().queryAABB(detectionCallback, aabb);
    }


    public boolean isbot(){
        return isbot;
    }

    public SpaceShip setAttackMode() {
        this.body.m_linearDamping = attackModeLinearDamping;
        return (SpaceShip)this;
    }
    public SpaceShip setCruiseMode() {
        this.body.m_linearDamping = cruiseModeLinearDamping;
        return (SpaceShip)this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + (isbot ? 1231 : 1237);
        result = prime * result + (int) (timeCreated ^ (timeCreated >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
    	SpaceShip other = (SpaceShip) obj;
        if (id != other.getId())
            return false;
        return true;
    }

    public void autoheal(){
    	long now = new Date().getTime();
    	if(now-lasthealtime > getHealFrequency()){
    		int plus = getTotalEnergy()/10;
    		this.currentEnergy = this.currentEnergy + plus;
    		if(this.currentEnergy > this.getTotalEnergy()) 
    			this.currentEnergy =  this.getTotalEnergy();
    		lasthealtime = now;
    	}
    }
    
    public void restoreEnergy(){
    	currentEnergy = getTotalEnergy();
	}
    
    
    private long getHealFrequency() {
		return 5000;
	}
    
	public void setReadyToDestroy(boolean b){
		readyToDestroy = b;
	}
	
	public boolean readyToDestroy(){
		return readyToDestroy ;
	}
	
	public boolean isDamaged(){
		if(lastDamageTime < 0)
			return false;
		return (new Date().getTime() - lastDamageTime < damagePeriod );
	}
	
	public void setName(String name){
		this.name = name;    	
    }
	
    public String getName(){
    	return this.name;
    }

	protected abstract void init(Vec2 vec2);
    protected abstract int getTotalEnergy();
    protected abstract float getEnemyDetectRange();

}
