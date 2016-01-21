class Node:
    def __init__(self, val):
        self.val = val
        self.left = None
        self.right = None


class Tree:
    def __init__(self):
        self.root = None
        self.last = None
        self.current = None

    def get_val(self, val, node: Node = None)->Node:
        if not node:
            node = self.root
        if node.val == val:
            return node
        if node.left:
            ret = self.get_val(val, node.left)
            if ret:
                return ret
        if node.right:
            ret = self.get_val(val, node.right)
            if ret:
                return ret
        return None

    def insert_val(self, val, left=None, right=None):
        node = self.get_val(val)
        if node:
            if left:
                node.left = Node(left)
            if right:
                node.right = Node(right)

    def _get_path(self, val, node, path):
        path.append(node.val)
        if node.val == val:
            return path, True
        else:
            if node.left:
                new_path, ret = self._get_path(val, node.left, path)
                if ret is True:
                    return new_path, True
            if node.right:
                new_path, ret = self._get_path(val, node.right, path)
                if ret is True:
                    return new_path, True
        path.pop()
        return path, False

    def get_path(self, val)->[]:
        return self._get_path(val, self.root, [])[0]

