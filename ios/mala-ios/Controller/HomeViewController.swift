//
//  HomeViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let HomeViewCellReusedId = "HomeViewCellReusedId"

class HomeViewController: UICollectionViewController, DropViewDelegate {
    
    private lazy var teachers: [TeacherModel]? = nil
    private lazy var dropView: DropView = {
        let filterView = TeacherFilterView(frame: CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        let dropView = DropView(frame: CGRect(x: 0, y: 64-MalaContentHeight, width: MalaScreenWidth, height: MalaContentHeight), viewController: self, contentView: filterView)
        return dropView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        loadTeachers()
        
        // Register Cell Class
        self.collectionView!.registerClass(TeacherCollectionViewCell.self, forCellWithReuseIdentifier: HomeViewCellReusedId)
        
        setupUserInterface()
        dropView.delegate = self
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Event
    @objc private func locationButtonDidClick() {
        let alertView = UIAlertView.init(title: nil, message: "目前只支持洛阳地区，其他地区正在拓展中", delegate: nil, cancelButtonTitle: "知道了")
        alertView.show()
    }
    
    @objc private func screeningButtonDidClick() {
        dropView.isShow ? dropView.dismiss() : dropView.show()
    }
    
    
    // MARK: - Consturcted
    init() {
        super.init(collectionViewLayout: CommonFlowLayout(type: .HomeView))
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - DataSource
    override func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.teachers?.count ?? 0
    }
    
    override func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(HomeViewCellReusedId, forIndexPath: indexPath) as! TeacherCollectionViewCell
        
        // Configure the cell
        //cell.backgroundColor = UIColor.lightGrayColor()
        cell.model = teachers![indexPath.row]
        
        return cell
    }
    
    
    // MARK: - Delegate
    override func collectionView(collectionView: UICollectionView, shouldHighlightItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    override func collectionView(collectionView: UICollectionView, shouldSelectItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    override func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        let teacherId = (collectionView.cellForItemAtIndexPath(indexPath) as! TeacherCollectionViewCell).model!.id
        let teacherDetail = TeacherDetailModel() // TODO: Network request
        let viewController = TeacherDetailController()
        viewController.model = teacherDetail
        viewController.view.backgroundColor = UIColor.lightGrayColor()
        
        self.navigationController?.pushViewController(viewController, animated: true)
    }
    
    func dropViewDidTapButtonForContentView(contentView: UIView) {
        let filterObj: ConditionObject = (contentView as! TeacherFilterView).filterObject
        print("Tap Condition -- \(filterObj)")

        //let filters: [String: AnyObject] = ["grade": 1, "subject": 1, "tag": 1]
        let filters: [String: AnyObject] = ["grade": filterObj.grade.id, "subject": filterObj.subject.id, "tags": filterObj.tag.id]
        loadTeachers(filters)

        dropView.dismiss()
    }
    
    
    // MARK: - private Function
    private func setupUserInterface() {
        
        collectionView?.backgroundColor = UIColor.whiteColor()
        
        // leftBarButtonItem
        navigationItem.leftBarButtonItem = UIBarButtonItem(customView: UIButton(title: "洛阳", imageName: "location_normal", target: self, action: "locationButtonDidClick"))
        // rightBarButtonItem
        navigationItem.rightBarButtonItem = UIBarButtonItem(customView: UIButton(imageName: "screening_normal", target: self, action: "screeningButtonDidClick"))
    }

    private func loadTeachers(filters: [String: AnyObject]? = nil) {
        NetworkTool.sharedTools.loadTeachers(filters) { result, error in
            // Error
            if error != nil {
                debugPrint("HomeViewController - loadTeachers Request Error")
                return
            }
            
            // Make sure Dict not nil
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("HomeViewController - loadTeachers Format Error")
                return
            }

            self.teachers = []
            let resultModel = ResultModel(dict: dict)
            if resultModel.results != nil {
                for object in ResultModel(dict: dict).results! {
                    if let dict = object as? [String: AnyObject] {
                        self.teachers!.append(TeacherModel(dict: dict))
                    }
                }
            }
            self.collectionView?.reloadData()
        }
    }
    
}
