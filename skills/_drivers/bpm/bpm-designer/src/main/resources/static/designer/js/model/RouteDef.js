class RouteDef {
    constructor(data) {
        this.routeDefId = data?.routeDefId || this._generateId();
        this.name = data?.name || '新路由';
        this.description = data?.description || '';
        this.from = data?.from || data?.fromActivityDefId || '';
        this.to = data?.to || data?.toActivityDefId || '';
        this.condition = data?.condition || data?.routeCondition || '';
        this.routeDirection = data?.routeDirection || 'FORWARD';
        this.routeConditionType = data?.routeConditionType || 'CONDITION';
        this.routeOrder = data?.routeOrder || 1;
        this.extendedAttributes = data?.extendedAttributes || {};
    }

    _generateId() {
        return 'route_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    toJSON() {
        return {
            routeDefId: this.routeDefId,
            name: this.name,
            description: this.description,
            from: this.from,
            to: this.to,
            condition: this.condition,
            routeDirection: this.routeDirection,
            routeConditionType: this.routeConditionType,
            routeOrder: this.routeOrder,
            extendedAttributes: this.extendedAttributes
        };
    }
}

window.RouteDef = RouteDef;
