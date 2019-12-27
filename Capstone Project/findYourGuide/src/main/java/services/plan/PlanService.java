package services.plan;

import entities.Plan;

public interface PlanService {
    public int createPlan(Plan newPlan);
    public Plan searchPlanByPlanId(int plan_id) throws Exception;
    public Plan searchPlanByPostId(int post_id) throws Exception;
    public void updatePlan(int post_id, String meeting_point, String detail) throws Exception;
}
