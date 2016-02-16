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
        model.avatar = "http://img.ivsky.com/img/tupian/img/201103/25/jiaoshi_texie-003.jpg"
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
    
    
    class func TeacherList() -> [TeacherModel] {
        let teacher1 = TeacherModel(
            id: 1,
            name: "顾思源",
            avatar: "http://img.ivsky.com/img/tupian/img/201103/25/jiaoshi_texie-027.jpg",
            degree: "麻辣金牌教师",
            minPrice: 100,
            maxPrice: 200,
            subject: "数学",
            shortname: "小初",
            tags: ["幽默", "有责任心", "正能量"]
        )
        let teacher2 = TeacherModel(
            id: 2,
            name: "Winston Lee",
            avatar: "http://img.ivsky.com/img/tupian/img/201103/25/jiaoshi_texie-027.jpg",
            degree: "权威奥数教练",
            minPrice: 150,
            maxPrice: 300,
            subject: "政治",
            shortname: "小初高",
            tags: ["幽默", "有责任心", "正能量"]
        )
        let teacher3 = TeacherModel(
            id: 1,
            name: "顾思源",
            avatar: "http://img.ivsky.com/img/tupian/img/201103/25/jiaoshi_texie-027.jpg",
            degree: "麻辣金牌教师",
            minPrice: 100,
            maxPrice: 200,
            subject: "数学",
            shortname: "小初",
            tags: ["幽默", "有责任心", "正能量"]
        )
        let teacher4 = TeacherModel(
            id: 1,
            name: "顾思源",
            avatar: "http://img.ivsky.com/img/tupian/img/201103/25/jiaoshi_texie-027.jpg",
            degree: "麻辣金牌教师",
            minPrice: 100,
            maxPrice: 200,
            subject: "数学",
            shortname: "小初",
            tags: ["幽默", "有责任心", "正能量"]
        )
        let teacher5 = TeacherModel(
            id: 1,
            name: "顾思源",
            avatar: "http://img.ivsky.com/img/tupian/img/201103/25/jiaoshi_texie-027.jpg",
            degree: "麻辣金牌教师",
            minPrice: 100,
            maxPrice: 200,
            subject: "数学",
            shortname: "小初",
            tags: ["幽默", "有责任心", "正能量"]
        )
        return [teacher1, teacher2, teacher3, teacher4, teacher5]
    }
    
    class func tags() -> [String] {
        return [
            "幽默",
            "常识教育",
            "100%进步率",
            "学员过千",
            "押题达人",
            "幽默风趣",
            "心理专家",
            "亲和力强",
            "公立学校老师",
            "最受学生欢迎",
            "80后名师",
            "英语演讲冠军",
            "麻辣金牌教师",
            "权威奥数教练"
        ]
    }
    
    class func classSchedule() -> [[ClassScheduleDayModel]] {
        let classData1 = [
            ClassScheduleDayModel(id: 1,start: "08:00", end: "10:00", available: false),
            ClassScheduleDayModel(id: 1,start: "10:00", end: "12:00", available: true),
            ClassScheduleDayModel(id: 1,start: "13:00", end: "15:00", available: true),
            ClassScheduleDayModel(id: 1,start: "15:00", end: "17:00", available: true),
            ClassScheduleDayModel(id: 1,start: "17:00", end: "19:00", available: true),
        ]
        let classData2 = [
            ClassScheduleDayModel(id: 1,start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 1,start: "10:00", end: "12:00", available: false),
            ClassScheduleDayModel(id: 1,start: "13:00", end: "15:00", available: true),
            ClassScheduleDayModel(id: 1,start: "15:00", end: "17:00", available: true),
            ClassScheduleDayModel(id: 1,start: "17:00", end: "19:00", available: true),
        ]
        let classData3 = [
            ClassScheduleDayModel(id: 1,start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 1,start: "10:00", end: "12:00", available: true),
            ClassScheduleDayModel(id: 1,start: "13:00", end: "15:00", available: false),
            ClassScheduleDayModel(id: 1,start: "15:00", end: "17:00", available: true),
            ClassScheduleDayModel(id: 1,start: "17:00", end: "19:00", available: true),
        ]
        let classData4 = [
            ClassScheduleDayModel(id: 1,start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 1,start: "10:00", end: "12:00", available: true),
            ClassScheduleDayModel(id: 1,start: "13:00", end: "15:00", available: false),
            ClassScheduleDayModel(id: 1,start: "15:00", end: "17:00", available: true),
            ClassScheduleDayModel(id: 1,start: "17:00", end: "19:00", available: true),
        ]
        let classData5 = [
            ClassScheduleDayModel(id: 1,start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 1,start: "10:00", end: "12:00", available: true),
            ClassScheduleDayModel(id: 1,start: "13:00", end: "15:00", available: true),
            ClassScheduleDayModel(id: 1,start: "15:00", end: "17:00", available: false),
            ClassScheduleDayModel(id: 1,start: "17:00", end: "19:00", available: true),
        ]
        let classData6 = [
            ClassScheduleDayModel(id: 1,start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 1,start: "10:00", end: "12:00", available: true),
            ClassScheduleDayModel(id: 1,start: "13:00", end: "15:00", available: true),
            ClassScheduleDayModel(id: 1,start: "15:00", end: "17:00", available: true),
            ClassScheduleDayModel(id: 1,start: "17:00", end: "19:00", available: false),
        ]
        let classData7 = [
            ClassScheduleDayModel(id: 1,start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 1,start: "10:00", end: "12:00", available: true),
            ClassScheduleDayModel(id: 1,start: "13:00", end: "15:00", available: true),
            ClassScheduleDayModel(id: 1,start: "15:00", end: "17:00", available: true),
            ClassScheduleDayModel(id: 1,start: "17:00", end: "19:00", available: true),
        ]
        
        return [classData1, classData2, classData3, classData4, classData5, classData6, classData7]
    }
    
    
    class func testDate() {
        let array = [
            ClassScheduleDayModel(id: 5, start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 5, start: "10:30", end: "12:30", available: true),
            ClassScheduleDayModel(id: 3, start: "08:00", end: "10:00", available: true)
        ]
        
        let resultArray = ThemeDate.dateArray(array, period: 6)
        print(resultArray)
    }
    
    class func testDateInThisWeek() {
        print(NSDate().weekday())
        print("周一".dateInThisWeek().formattedDateWithFormat("YYYY/MM/dd"))
    }
 }
