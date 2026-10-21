package com.chopcal.app.page.onboarding.step

import com.chopcal.app.domain.model.UserProfile

/**
 * 流程注册表 —— 扩展核心。
 *
 * 新增/删除/调序步骤，只需改 [resolve] 里的顺序和分支条件，
 * 导航、进度条、返回逻辑自动生效。
 */
object OnboardingFlow {

    /** 根据当前草稿解析出当前分支的步骤顺序（保持体重时不含 TARGET）。 */
    fun resolve(draft: UserProfile): List<StepId> = buildList {
        add(StepId.WELCOME)
        add(StepId.GOAL)
        add(StepId.BASIC_INFO)
        add(StepId.BODY_DATA)
        add(StepId.BMI_FEEDBACK)
        if (draft.goalType?.needsTargetWeight == true) {
            add(StepId.TARGET)
        }
        add(StepId.ACTIVITY)
        add(StepId.RESULT)
        add(StepId.FINISH)
    }
}
