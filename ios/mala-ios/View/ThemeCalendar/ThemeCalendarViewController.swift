//
//  ThemeCalendarViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/4/8.
//  Copyright © 2016年 Mala Online. All rights reserved.
//


import UIKit

///  Theme日历组件协议
@objc public protocol ThemeCalendarViewDelegate: NSObjectProtocol {
    optional func calendarViewControllerIsEnabledDate(controller: ThemeCalendarViewController, date: NSDate) -> Bool
    optional func calendarViewControllerDidSelectedDate(controller: ThemeCalendarViewController, date: NSDate)
    optional func calendarViewControllerShouldUseCustomColorsForDate(controller: ThemeCalendarViewController, date: NSDate) -> Bool
    optional func calendarViewControllerCircleColorForDate(controller: ThemeCalendarViewController, date: NSDate) -> UIColor
    optional func calendarViewControllerTextColorForDate(controller: ThemeCalendarViewController, date: NSDate) -> UIColor
}


private let ThemeCalendarOverlaySize: CGFloat = 14.0
private let ThemeCalendarViewCellReuseID = "ThemeCalendarViewCellReuseID"
private let ThemeCalendarViewHeaderReuseID = "ThemeCalendarViewHeaderReuseID"
private let kCalendarUnitYMD: NSCalendarUnit = [.Year, .Month, .Day]

public class ThemeCalendarViewController: UICollectionViewController, ThemeCalendarViewCellDelegate {
    
    // MARK: - Propery
    /// 日历对象
    public var calendar: NSCalendar = {
        let calendar = NSCalendar.currentCalendar()
        return calendar
        }() {
        didSet {
            self.headerDateFormatter.calendar = calendar
            self.daysPerWeek = calendar.maximumRangeOfUnit(.Weekday).length
        }
    }
    /// 开始时间
    public var firstDate: NSDate {
        set {
            self.firstDate = self.clampDate(firstDate, unitFlags: kCalendarUnitYMD)
        }
        get {
            let components = self.calendar.components(kCalendarUnitYMD, fromDate: NSDate())
            components.month = 3
            components.day = 1
            let date = self.calendar.dateFromComponents(components)!
            return date
        }
    }
    /// 结束时间
    public var lastDate: NSDate {
        set {
            self.lastDate = self.clampDate(lastDate, unitFlags: kCalendarUnitYMD)
        }
        get {
            let offsetComponents = NSDateComponents()
            offsetComponents.year = 1
            offsetComponents.day = -1
            let date = self.calendar.dateByAddingComponents(offsetComponents, toDate: self.firstDateMonth, options: NSCalendarOptions())!
            return date
        }
    }
    /// 当前选择时间
    public var selectedDate: NSDate? {
        willSet {
            
            guard selectedDate != nil else {
                return
            }
            
            //if newSelectedDate is nil, unselect the current selected cell
            if newValue == nil {
                self.cellForItemAtDate(selectedDate!).selected = false
                self.selectedDate = newValue
                return
            }
            
            //Test if selectedDate between first & last date
            let startOfDay: NSDate? = self.clampDate(newValue!, unitFlags: kCalendarUnitYMD)
            
            guard startOfDay != nil else { return }
            
            if (startOfDay!.compare(self.firstDateMonth) == .OrderedAscending ||  startOfDay!.compare(self.lastDateMonth) == .OrderedDescending) {
                //the newSelectedDate is not between first & last date of the calendar, do nothing.
                return
            }
            
            self.cellForItemAtDate(selectedDate!).selected = false
            self.cellForItemAtDate(startOfDay!).selected = true
            
            self.selectedDate = startOfDay!
            
            if let indexPath = self.indexPathForCellAtDate(selectedDate!) {
                self.collectionView?.reloadItemsAtIndexPaths([indexPath])
            }
            
            // Notify the delegate
            self.delegate?.calendarViewControllerDidSelectedDate?(self, date: self.selectedDate!)
        }
    }
    /// 背景颜色
    public var backgroundColor: UIColor = UIColor.whiteColor()
    /// 背景颜色
    public var overlayTextColor: UIColor = MalaColor_333333_0 {
        didSet {
            self.overlayView.textColor = self.overlayTextColor
        }
    }
    /// 背景颜色
    public var overlayBackgroundColor: UIColor = MalaColor_F2F2F2_0
    /// 是否显示星期视图标识
    public var weekdayHeaderEnabled: Bool = true
    /// 星期文字显示样式
    public var weekdayTextType: ThemeCalendarViewWeekdayTextType = .VeryShort
    /// 代理对象
    public var delegate: ThemeCalendarViewDelegate?
    /// 日期显示视图
    internal var overlayView: UILabel = {
        let overlayView = UILabel()
        overlayView.text = "2016年1月1日"
        overlayView.font = UIFont.systemFontOfSize(ThemeCalendarOverlaySize)
        overlayView.alpha = 0.0
        overlayView.textAlignment = .Center
        overlayView.numberOfLines = 0
        overlayView.sizeToFit()
        return overlayView
    }()
    /// 头部视图日期格式化组件(滑动时显示)
    internal lazy var headerDateFormatter: NSDateFormatter = {
        let formatter = NSDateFormatter()
        formatter.calendar = self.calendar
        formatter.dateFormat = NSDateFormatter.dateFormatFromTemplate("yyyy LLLL", options: 0, locale: self.calendar.locale)
        return formatter
    }()
    /// 星期视图日期格式化组件
    internal lazy var weekdayheaderDateFormatter: NSDateFormatter = {
        let formatter = NSDateFormatter()
        formatter.calendar = self.calendar
        formatter.dateFormat = NSDateFormatter.dateFormatFromTemplate("M/M", options: 0, locale: self.calendar.locale)
        return formatter
    }()
    /// 星期信息显示视图
    internal var weekdayHeader: ThemeCalendarViewWeekdayHeader!
    internal lazy var firstDateMonth: NSDate = {
        let components = self.calendar.components(kCalendarUnitYMD, fromDate: self.firstDate)
        components.day = 1
        let date = self.calendar.dateFromComponents(components)!
        return date
    }()
    internal lazy var lastDateMonth: NSDate = {
        let components = self.calendar.components(kCalendarUnitYMD, fromDate: self.lastDate)
        components.month += 1
        components.day = 0
        let date = self.calendar.dateFromComponents(components)!
        return date
    }()
    /// 每周天数
    internal var daysPerWeek: Int = 7
    
    
    // MARK: - Constructed
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(collectionViewLayout: ThemeCalendarViewFlowLayout())
        configure()
    }
    
    override init(collectionViewLayout layout: UICollectionViewLayout) {
        super.init(collectionViewLayout: layout)
        configure()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(collectionViewLayout: ThemeCalendarViewFlowLayout())
        configure()
    }
    
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        // 配置CollectionView
        self.collectionView!.registerClass(ThemeCalendarViewCell.self, forCellWithReuseIdentifier: ThemeCalendarViewCellReuseID)
        self.collectionView?.registerClass(ThemeCalendarViewHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: ThemeCalendarViewHeaderReuseID)
        
        self.collectionView?.delegate = self
        self.collectionView?.dataSource = self
        self.collectionView?.backgroundColor = self.backgroundColor
        
        // 配置weekdayHeader
        self.weekdayHeader = ThemeCalendarViewWeekdayHeader(calendar: self.calendar, textType: self.weekdayTextType)
        self.view.addSubview(weekdayHeader)
        weekdayHeader.translatesAutoresizingMaskIntoConstraints = false
        
        // 配置overlayView
        self.overlayView.backgroundColor = self.overlayBackgroundColor
        self.overlayView.textColor = self.overlayTextColor
        self.view.insertSubview(overlayView, aboveSubview: weekdayHeader)
        overlayView.translatesAutoresizingMaskIntoConstraints = false
        
        let weekdayHeaderHeight = self.weekdayHeaderEnabled ? ThemeCalendarViewWeekdayHeaderHeight : 0
        let viewsDictionary: [String: AnyObject] = ["overlayView": self.overlayView, "weekdayHeader": self.weekdayHeader]
        let metricsDictionary: [String: AnyObject] = ["overlayViewHeight": ThemeCalendarViewFlowLayoutHeaderHeight, "weekdayHeaderHeight": weekdayHeaderHeight]
        
        self.view.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "|[weekdayHeader]|",
                options: .AlignAllTop,
                metrics: nil,
                views: viewsDictionary
            )
        )
        self.view.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "|[overlayView]|",
                options: .AlignAllTop,
                metrics: nil,
                views: viewsDictionary
            )
        )
        self.view.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "V:|[weekdayHeader(weekdayHeaderHeight)]",
                options: .DirectionLeadingToTrailing,
                metrics: metricsDictionary,
                views: viewsDictionary
            )
        )
        self.view.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "V:|[overlayView(overlayViewHeight)]",
                options: .DirectionLeadingToTrailing,
                metrics: metricsDictionary,
                views: viewsDictionary
            )
        )
        self.collectionView?.contentInset = UIEdgeInsets(top: weekdayHeaderHeight, left: 0, bottom: 0, right: 0)
    }
    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    public override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        if self.selectedDate != nil {
            self.collectionViewLayout.invalidateLayout()
        }
    }
    
    
    // MARK: - Private Method
    private func configure() {
        
    }
    
    
    // MARK: - DataSource
    public override func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return self.calendar.components(.Month, fromDate: self.firstDateMonth, toDate: self.lastDateMonth, options: .WrapComponents).month + 1
    }
    
    public override func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        let firstOfMonth = self.firstOfMonthForSection(section)
        let weekCalendarUnit: NSCalendarUnit = .WeekOfMonth
        let rangeOfWeeks = self.calendar.rangeOfUnit(weekCalendarUnit, inUnit: .Month, forDate: firstOfMonth)
        
        //We need the number of calendar weeks for the full months (it will maybe include previous month and next months cells)
        return (rangeOfWeeks.length * self.daysPerWeek)
    }
    
    public override func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(ThemeCalendarViewCellReuseID, forIndexPath: indexPath) as! ThemeCalendarViewCell
        cell.delegate = self
        
        let firstOfMonth = self.firstOfMonthForSection(indexPath.section)
        let cellDate = self.dateForCellAtIndexPath(indexPath)
        
        
        let cellDateComponents = self.calendar.components(kCalendarUnitYMD, fromDate: cellDate)
        let firstOfMonthsComponents = self.calendar.components(kCalendarUnitYMD, fromDate: firstOfMonth)
        
        var isToday = false
        var isSelected = false
        var isCustomDate: Bool? = false
        
        if cellDateComponents.month == firstOfMonthsComponents.month {
            isSelected = self.isSelectedDate(cellDate) && (indexPath.section == self.sectionForDate(cellDate))
            isToday = self.isTodayDate(cellDate)
            cell.setDate(cellDate, calendar: self.calendar)
            isCustomDate = self.delegate?.calendarViewControllerShouldUseCustomColorsForDate?(self, date: cellDate)
        }else {
            cell.setDate(nil, calendar: nil)
        }
        
        if isToday {
            cell.isToday = true
        }
        
        if isSelected {
            cell.selected = true
        }
        
        if self.isEnabledDate(cellDate) || (isCustomDate == true) {
            cell.refreshCellColors()
        }
        
        cell.layer.shouldRasterize = true
        cell.layer.rasterizationScale = UIScreen.mainScreen().scale
        
        return cell
    }
    
    
    // MARK: - Delegate
    public override func collectionView(collectionView: UICollectionView, shouldSelectItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        let firstOfMonth = self.firstOfMonthForSection(indexPath.section)
        let cellDate = self.dateForCellAtIndexPath(indexPath)
        
        if !self.isEnabledDate(cellDate) {
            return false
        }
        
        let cellDateComponents = self.calendar.components([.Day, .Month], fromDate: cellDate)
        let firstOfMonthsComponents = self.calendar.components(.Month, fromDate: firstOfMonth)
        
        return (cellDateComponents.month == firstOfMonthsComponents.month)
    }
    
    public override func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        self.selectedDate = self.dateForCellAtIndexPath(indexPath)
    }
    
    public override func collectionView(collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, atIndexPath indexPath: NSIndexPath) -> UICollectionReusableView {
        if kind == UICollectionElementKindSectionHeader {
            let headerView = self.collectionView?.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionHeader, withReuseIdentifier: ThemeCalendarViewHeaderReuseID, forIndexPath: indexPath) as! ThemeCalendarViewHeader
            headerView.titleLabel.text = self.weekdayheaderDateFormatter.stringFromDate(self.firstOfMonthForSection(indexPath.section)).uppercaseString
            headerView.layer.shouldRasterize = true
            headerView.layer.rasterizationScale = UIScreen.mainScreen().scale
            return headerView
        }
        return UICollectionReusableView()
    }
    
    // MARK: - UICollectionViewFlowLayoutDelegate
    public func collectionView(collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAtIndexPath indexPath: NSIndexPath) -> CGSize {
        let itemWidth = floor(CGRectGetWidth(self.collectionView!.bounds) / CGFloat(self.daysPerWeek))
        return CGSize(width: itemWidth, height: 85/*itemWidth*/)
    }
    
    // MARK: - UIScrollViewDelegate
    public override func scrollViewWillEndDragging(scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
        //We only display the overlay view if there is a vertical velocity
        if (fabs(velocity.y) > 0.0) {
            if (self.overlayView.alpha < 1.0) {
                self.overlayView.alpha = 1.0
            }
        }
    }
    
    public override func scrollViewDidEndDragging(scrollView: UIScrollView, willDecelerate decelerate: Bool) {
//        let duration: NSTimeInterval = 2 /*decelerate ? 1.5 : 0.0*/
//        delay(duration) {
//            self.hideOverlayView()
//        }
    }
    
    public override func scrollViewDidEndDecelerating(scrollView: UIScrollView) {
        self.hideOverlayView()
    }
    
    public override func scrollViewDidScroll(scrollView: UIScrollView) {
        let indexPaths: NSArray = self.collectionView!.indexPathsForVisibleItems()
        let sortedIndexPaths = indexPaths.sortedArrayUsingSelector(#selector(NSIndexPath.compare(_:)))
        
        guard sortedIndexPaths.count != 0 else {
            return
        }
        
        let firstIndexPath = sortedIndexPaths.first as! NSIndexPath
        self.overlayView.text = self.headerDateFormatter.stringFromDate(self.firstOfMonthForSection(firstIndexPath.section))
    }
    
    public func hideOverlayView() {
        UIView.animateWithDuration(0.25) { 
            self.overlayView.alpha = 0.0
        }
    }
    
    
    // MARK: - Calendar calculations
    func clampDate(date: NSDate, unitFlags: NSCalendarUnit) -> NSDate {
        let components = self.calendar.components(unitFlags, fromDate: date)
        return self.calendar.dateFromComponents(components)!
    }
    
    func isTodayDate(date: NSDate) -> Bool {
        return self.clampAndCompareDate(date, referenceDate: NSDate())
    }
    
    func isSelectedDate(date: NSDate) -> Bool {
        guard let selectDate = self.selectedDate else {
            return false
        }
        return self.clampAndCompareDate(date, referenceDate: selectDate)
    }
    
    func isEnabledDate(date: NSDate) -> Bool {
        let clampedDate = self.clampDate(date, unitFlags: kCalendarUnitYMD)
        if clampedDate.compare(self.firstDate) == .OrderedAscending || clampedDate.compare(self.lastDate) == .OrderedDescending {
            return false
        }
        self.delegate?.calendarViewControllerIsEnabledDate?(self, date: date)
        return true
    }
    
    func clampAndCompareDate(date: NSDate, referenceDate: NSDate) -> Bool {
        let refDate = self.clampDate(referenceDate, unitFlags: kCalendarUnitYMD)
        let clampedDate = self.clampDate(date, unitFlags: kCalendarUnitYMD)
        return refDate.isEqualToDate(clampedDate)
    }
    
    // MARK: - Calendar Method
    func firstOfMonthForSection(section: Int) -> NSDate {
        let offset = NSDateComponents()
        offset.month = section
        return (self.calendar.dateByAddingComponents(offset, toDate: self.firstDateMonth, options: NSCalendarOptions()))!
    }
    
    func sectionForDate(date: NSDate) -> Int? {
        return (self.calendar.components(.Month, fromDate: self.firstDateMonth, toDate: date, options: NSCalendarOptions()).month)
    }
    
    func dateForCellAtIndexPath(indexPath: NSIndexPath) -> NSDate {
        let firstOfMonth = self.firstOfMonthForSection(indexPath.section)
        let weekday = self.calendar.components(.Weekday, fromDate: firstOfMonth).weekday
        var startOffset = weekday - self.calendar.firstWeekday
        startOffset += (startOffset >= 0 ? 0 : self.daysPerWeek)
        
        let dateComponents = NSDateComponents()
        dateComponents.day = indexPath.item - startOffset
        
        return (self.calendar.dateByAddingComponents(dateComponents, toDate: firstOfMonth, options: NSCalendarOptions()))!
    }
    
    private let kFirstDay = 1
    func indexPathForCellAtDate(date: NSDate?) -> NSIndexPath? {
        guard date != nil else {
            return nil
        }
        
        let section = self.sectionForDate(date!)!
        let firstOfMonth = self.firstOfMonthForSection(section)
        
        let weekday = self.calendar.components(.Weekday, fromDate: firstOfMonth).weekday
        var startOffset = weekday - self.calendar.firstWeekday
        startOffset += startOffset >= 0 ? 0 : self.daysPerWeek
        
        let day = self.calendar.components(kCalendarUnitYMD, fromDate: date!).day
        let item = (day - kFirstDay + startOffset)
        
        return NSIndexPath(forItem: item, inSection: section)
    }
    
    func cellForItemAtDate(date: NSDate) -> ThemeCalendarViewCell {
        return self.collectionView?.cellForItemAtIndexPath(self.indexPathForCellAtDate(date)!) as! ThemeCalendarViewCell
    }
    
    
    // MARK: - Public Method
    public func scrollToSelectedDate(date: NSDate, animated: Bool) {
        if selectedDate != nil {
            self.scrollToDate(selectedDate!, animated: animated)
        }
    }
    
    public func scrollToDate(date: NSDate, animated: Bool) {
        
        do {
            if let selectedDateIndexPath = self.indexPathForCellAtDate(date) {
                if (self.collectionView?.indexPathsForVisibleItems().contains(selectedDateIndexPath)) == false {
                    let sectionIndexPath = NSIndexPath(forItem: 0, inSection: selectedDateIndexPath.section)
                    if let sectionLayoutAttributes = self.collectionView?.layoutAttributesForItemAtIndexPath(sectionIndexPath) {
                        var origin = sectionLayoutAttributes.frame.origin
                        origin.x = 0
                        origin.y -= ThemeCalendarViewFlowLayoutHeaderHeight + ThemeCalendarViewFlowLayoutInsetTop + (self.collectionView?.contentInset.top ?? 0)
                        self.collectionView?.setContentOffset(origin, animated: animated)
                    }
                }
            }
        }/*catch{
            
        }*/
    }
    
    public override func willRotateToInterfaceOrientation(toInterfaceOrientation: UIInterfaceOrientation, duration: NSTimeInterval) {
        self.collectionView?.collectionViewLayout.invalidateLayout()
    }
    
    
    
    // MARK: - ControllerDelegate
    public func cellShouldUseCustomColorsForDate(cell: ThemeCalendarViewCell, date: NSDate) -> Bool {
        if !self.isEnabledDate(date) {
            return true
        }
        self.delegate?.calendarViewControllerShouldUseCustomColorsForDate?(self, date: date)
        return false
    }
    
    public func cellTextColorForDate(cell: ThemeCalendarViewCell, date: NSDate?) -> UIColor {
        guard date != nil else {
            return cell.circleDefaultColor!
        }
        
        if !self.isEnabledDate(date!) {
            return cell.circleDefaultColor!
        }
        
        return (self.delegate?.calendarViewControllerCircleColorForDate?(self, date: date!))!
    }
    
    public func cellCircleColorForDate(cell: ThemeCalendarViewCell, date: NSDate?) -> UIColor {
        guard date != nil else {
            return cell.textDisabledColor!
        }
        
        if !self.isEnabledDate(date!) {
            return cell.textDisabledColor!
        }
        
        return (self.delegate?.calendarViewControllerTextColorForDate?(self, date: date!))!
    }
}