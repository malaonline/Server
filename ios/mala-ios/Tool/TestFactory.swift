//
//  TestFactory.swift
//  mala-ios
//
//  Created by Elors on 1/12/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TestFactory {

    
    // MARK: - Model
    class func TeacherDetailsModel() -> TeacherDetailModel {
        
        let model = TeacherDetailModel()
        model.id = 1
        model.avatar = "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img0_68YMpeT.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=27a93b2d31f80809c5c1f54ff3f4b9855dea3bfd3593921a1fdcab571ef969cd&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z"
        model.gender = "fm"
        model.name = "丁思甜"
        model.degree = "s"
        model.teaching_age = 27
        model.level = "麻辣合伙人"
        model.subject = "数学"
        model.grades = ["小升初", "初中"]
        model.tags = ["幽默", "亲切", "专治厌学"]
        model.photo_set = [
            "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img0_68YMpeT.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=27a93b2d31f80809c5c1f54ff3f4b9855dea3bfd3593921a1fdcab571ef969cd&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z",
            "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img1_YHYtvWX.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=b0001bc1133bb6229d60b9b99a413bb2bfbf15537215ff30a141ddbcce3b614e&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z",
            "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img2_mOkzmQw.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=7bb0761fa15a491ed437588f81557938ba29d0b49b35f31d61f063f37c24c866&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z"]
        model.certificate_set = ["特级教师","一级教师","十佳青年"]
        model.highscore_set = [
            HighScoreModel(name: "高明", score: 122, school: "洛阳一中", admitted: "河北大学"),
            HighScoreModel(name: "高晓明", score: 125, school: "洛阳二中", admitted: "北京大学"),
            HighScoreModel(name: "ElorsAt", score: 163, school: "洛阳三中", admitted: "中央美院"),
        ]
        model.prices = [
            GradePriceModel(name: "小学", id: 4121, price: 99),
            GradePriceModel(name: "初中基础一对一", id: 2133, price: 199),
            GradePriceModel(name: "高中数学", id: 4241, price: 399)
        ]
        return model
    }
    
}
