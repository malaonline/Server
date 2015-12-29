//
//  TeacherCollectionViewCell.swift
//  mala-ios
//
//  Created by Erdi on 12/28/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import Kingfisher

class TeacherCollectionViewCell: UICollectionViewCell {

    // MARK: - Variables
    var model: TeacherModel? {
        didSet{

            // set avatar
            if model!.avatar != nil {
                imageView.kf_setImageWithURL(model!.avatar!,
                    placeholderImage: nil)
                //optionsInfo: [.Options(KingfisherOptions.ForceRefresh)])
            }
            // set price
            priceLabel.text = String(format: "%d-%d¥/课时", model!.min_price, model!.max_price)
            // set name
            nameLabel.text = model!.name
            nameLabel.sizeToFit()
            // set subject
            let subjectText = MalaSubject[model!.subject] ?? "-"
            // set grades
            var gradesText: String {
                var result = "-"
                if MalaGrades.instance.data.count != 3 {
                    print("Error: Grade levels should be 3")
                    return result
                }
                if let grades = model!.grades {
                    var shortGradeNameArray = [String]()
                    for grade in grades.sort() {
                        if grade < MalaGrades.instance.data[1].id {
                            // 小学
                            if !shortGradeNameArray.contains("小") {
                                shortGradeNameArray.append("小")
                            }
                            result = MalaGrades.instance.data[0].name!
                        } else if grade < MalaGrades.instance.data[2].id {
                            // 初中
                            if !shortGradeNameArray.contains("初") {
                                shortGradeNameArray.append("初")
                            }
                            result = MalaGrades.instance.data[1].name!
                        } else {
                            // 高中
                            if !shortGradeNameArray.contains("高") {
                                shortGradeNameArray.append("高")
                            }
                            result = MalaGrades.instance.data[2].name!
                        }
                    }
                    if shortGradeNameArray.count > 1 {
                        result.removeAll()
                        for shortGradeName in shortGradeNameArray {
                            result += shortGradeName
                        }
                    }
                }
                return result
            }
            gradeSubjectLabel.text = String(format: "%@・%@", gradesText, subjectText)
            gradeSubjectLabel.sizeToFit()
            print(gradeSubjectLabel.text)

            // set tags
            // Todo: these tags data must be saved first that can be displayed on teachers list page
            // the data will retrive directly from "teachers" API later
            var tagsText: String {
                var result = "-"
                if let tags = model!.tags {
                    result.removeAll()
                    let test: [String] = tags.map({ (tag) -> String in
                        if let tagName = MalaTeacherTags.instance.data![tag] {
                            return tagName
                        } else {
                            return "-"
                        }
                    })
                    result = test.joinWithSeparator("・")
                }
                return result
            }
            print(tagsText)
            //tagsText.removeAtIndex(tagsText.endIndex)
            tagsLabel.text = tagsText
            tagsLabel.sizeToFit()
        }
    }

    // MARK: - Components
    private lazy var imageView: UIImageView = UIImageView()
    private lazy var priceLabel: UILabel = UILabel()
    private lazy var nameLabel: UILabel = UILabel()
    private lazy var gradeSubjectLabel: UILabel = UILabel()
    private lazy var tagsLabel: UILabel = UILabel()

    // MARK: - Constructed
    override init(frame: CGRect) {
        self.model = nil
        super.init(frame: frame)

        setupUI()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: - Setup Cell UI
    private func setupUI() {

        imageView.addSubview(priceLabel)
        self.addSubview(imageView)
        self.addSubview(nameLabel)
        self.addSubview(gradeSubjectLabel)
        self.addSubview(tagsLabel)

        imageView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top)
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(imageView.snp_width)
        }

        priceLabel.backgroundColor = UIColor.blackColor()
        priceLabel.textColor = MalaAppearanceTextColor
        priceLabel.textAlignment = .Right
        priceLabel.alpha = 0.5
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(imageView.snp_left)
            make.right.equalTo(imageView.snp_right)
            make.bottom.equalTo(imageView.snp_bottom)
        }

        nameLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(imageView.snp_bottom).offset(10)
        }

        gradeSubjectLabel.textColor = UIColor.redColor()
        gradeSubjectLabel.font = UIFont.systemFontOfSize(14)
        gradeSubjectLabel.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(nameLabel.snp_bottom)
            make.right.equalTo(self.snp_right)
        }

        tagsLabel.textColor = UIColor.grayColor()
        tagsLabel.font = UIFont.systemFontOfSize(14)
        tagsLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(nameLabel.snp_bottom).offset(4)
        }
    }
}
