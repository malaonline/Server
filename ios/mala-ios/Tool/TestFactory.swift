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
//        model.achievement_set = ["特级教师","一级教师","十佳青年"]
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
    
    class func testStudentCourseData() -> [StudentCourseModel] {
        return [
            StudentCourseModel(id: 1, end: NSTimeInterval(1457298000), subject: "数学", is_passed: true, is_commented: true), //2016.3.7
            StudentCourseModel(id: 11, end: NSTimeInterval(1457298030), subject: "数学", is_passed: true, is_commented: true), //2016.3.7
            StudentCourseModel(id: 2, end: NSTimeInterval(1458334800), subject: "语文", is_passed: true, is_commented: true), //2016.3.19
            StudentCourseModel(id: 3, end: NSTimeInterval(1458338400), subject: "语文", is_passed: true, is_commented: true), //2016.3.19
            StudentCourseModel(id: 4, end: NSTimeInterval(1458324000), subject: "语文", is_passed: true, is_commented: true), //2016.3.19
            StudentCourseModel(id: 5, end: NSTimeInterval(1458928800), subject: "英语", is_passed: true, is_commented: true), //2016.3.30
            StudentCourseModel(id: 6, end: NSTimeInterval(1459951200), subject: "物理", is_passed: true, is_commented: true), //2016.4.6
            StudentCourseModel(id: 7, end: NSTimeInterval(1459911600), subject: "物理", is_passed: true, is_commented: true), //2016.4.6
            StudentCourseModel(id: 8, end: NSTimeInterval(1462935600), subject: "生物", is_passed: true, is_commented: true), //2016.5.11
        ]
    }
    
    class func testOrderForms() -> [OrderForm] {
        return [
            OrderForm(orderId: "1127482942338491", orderStatus: "u", teacherName: "王新宇1", subjectName: "生物", gradeName: "高二", schoolName: "洛阳市麻辣社区中心1", avatarURL: "http://img1.dwstatic.com/bdota/1411/279379325368/1415442422209.jpg", amount: 300),
            OrderForm(orderId: "1127482942338491", orderStatus: "p", teacherName: "王新宇2", subjectName: "物理", gradeName: "高二", schoolName: "洛阳市麻辣社区中心2", avatarURL: "http://img1.dwstatic.com/bdota/1411/279379325368/1415442422209.jpg", amount: 1200),
            OrderForm(orderId: "1127482942338491", orderStatus: "d", teacherName: "王新宇2", subjectName: "化学", gradeName: "高二", schoolName: "洛阳市麻辣社区中心3", avatarURL: "http://img1.dwstatic.com/bdota/1411/279379325368/1415442422209.jpg", amount: 700),
            OrderForm(orderId: "1127482942338491", orderStatus: "r", teacherName: "王新宇3", subjectName: "地理", gradeName: "高二", schoolName: "洛阳市麻辣社区中心4", avatarURL: "http://img1.dwstatic.com/bdota/1411/279379325368/1415442422209.jpg", amount: 900),
        ]
    }
    
    
    class func testCourseModels() -> [CourseModel] {
        return [
            CourseModel(id: 65, start: 1457942400, end: 1457949600, subject: "物理", school: "洛阳社区三店", is_passed: true, teacher: TeacherModel(), comment: CommentModel(id: 15, timeslot: 65, score: 2, content: "这个老师还行")),
            CourseModel(id: 66, start: 1457789400, end: 1457796600, subject: "物理", school: "洛阳社区三店", is_passed: true, teacher: TeacherModel(), comment: CommentModel(id: 15, timeslot: 65, score: 2, content: "这个老师还行")),
            CourseModel(id: 67, start: 1457797200, end: 1457804400, subject: "物理", school: "洛阳社区三店", is_passed: true, teacher: TeacherModel(), comment: CommentModel(id: 15, timeslot: 65, score: 2, content: "这个老师还行")),
        ]
    }
    
    class func testDateInThisWeek() {
        println(NSDate().weekday())
        println("周一".dateInThisWeek().formattedDateWithFormat("YYYY/MM/dd"))
    }
    
    class func testPingppPayment(charge: JSONDictionary) {
        
        let object = charge as NSDictionary
        
        Pingpp.createPayment(object,
            viewController: UIViewController(),
            appURLScheme: "alipay") { (result, error) -> Void in
                if result == "success" {
                    // 支付成功
                    println("支付成功")
                }else {
                    // 支付失败或取消
                    println("支付失败或取消")
                }
        }
    }
    
    class func testCoupons() {
        getCouponList({ (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("LoginViewController - VerifyCode Error \(errorMessage)")
            }
            }) { (coupons) -> Void in
                println("优惠券列表： \(coupons)")
        }
    }
    
 }
