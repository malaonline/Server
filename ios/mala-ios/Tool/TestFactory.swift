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
        model.avatar = "http://img.ivsky.com/img/tupian/img/201103/25/jiaoshi_texie-003.jpg     "
        model.gender = "fm"
        model.name = "丁思甜"
        model.degree = "s"
        model.teaching_age = 27
        model.level = "麻辣合伙人"
        model.subject = "数学"
        model.grades = ["小升初", "初中"]
        model.tags = ["幽默", "亲切", "专治厌学"]
        model.photo_set = [
            "http://img.taopic.com/uploads/allimg/110311/6446-110311151K931.jpg",
            "http://img.taopic.com/uploads/allimg/110821/1942-110r110431925.jpg",
            "http://www.86ps.com/sc/RW/311/GM_0024.jpg"]
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
