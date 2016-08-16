//
//  CityTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/8/16.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let CityTableViewCellReuseId = "CityTableViewCellReuseId"
private let CityTableViewHeaderViewReuseId = "CityTableViewHeaderViewReuseId"

class CityTableView: UITableView, UITableViewDelegate, UITableViewDataSource {

    // MARK: - Property
    // 城市数据模型
    var models: [BaseObjectModel] = [] {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.reloadData()
            })
        }
    }
    // 选择闭包
    var didSelectAction: (()->())?
    // 关闭闭包
    var closeAction: (()->())?
    
    
    // MARK: - Instance Method
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        delegate = self
        dataSource = self
        registerClass(CityTableViewCell.self, forCellReuseIdentifier: CityTableViewCellReuseId)
        registerClass(CityTableViewHeaderView.self, forHeaderFooterViewReuseIdentifier: CityTableViewHeaderViewReuseId)
        
        backgroundColor = MalaColor_F6F7F9_0
        separatorStyle = .None
        
    }
    
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = tableView.dequeueReusableHeaderFooterViewWithIdentifier(CityTableViewHeaderViewReuseId)
        return headerView
    }
    
    func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 40
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        MalaCurrentRegion = models[indexPath.row]
        didSelectAction?()
        closeAction?()
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return models.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CityTableViewCellReuseId, forIndexPath: indexPath) as! CityTableViewCell
        cell.model = models[indexPath.row]
        return cell
    }
    
}


class CityTableViewCell: UITableViewCell {
    
    // MARK: - Property
    // 城市数据模型
    var model: BaseObjectModel = BaseObjectModel() {
        didSet {
            textLabel?.text = model.name
        }
    }
    
    
    // MARK: - Instance Method
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        backgroundColor = MalaColor_F6F7F9_0
        selectionStyle = .None
        
        textLabel?.font = UIFont.systemFontOfSize(14)
    }
}


class CityTableViewHeaderView: UITableViewHeaderFooterView {

    // MARK: - Components
    private lazy var titleLabel: UILabel = {
        let label = UILabel(title: "选择服务城市")
        label.font = UIFont.systemFontOfSize(14)
        return label
    }()
    private lazy var separatorLine: UIView = {
        let view = UIView.separator(MalaColor_DEDFD0_0)
        return view
    }()
    
    
    // MARK: - Instance Method
    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        backgroundColor = MalaColor_F6F7F9_0
        
        // SubViews
        addSubview(titleLabel)
        addSubview(separatorLine)
        
        // AutoLayout
        titleLabel.snp_makeConstraints { (make) in
            make.bottom.equalTo(self.snp_bottom).offset(-8)
            make.left.equalTo(self.snp_left).offset(15)
            make.height.equalTo(20)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.left.equalTo(self.snp_left).offset(15)
            make.right.equalTo(self.snp_right).offset(-15)
            make.height.equalTo(MalaScreenOnePixel)
            make.bottom.equalTo(self.snp_bottom)
        }
    }
}