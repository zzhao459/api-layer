import { InputLabel } from '@mui/material';
import { Component } from 'react';
import BasicMenu from "../Menu/BasicMenu";
import VerifyAccessPanel from "../VerifyAccessPanel/VerifyAccessPanel";
import VerifyTrustedCertificate from '../VerifyTrustedCertificate/VerifyTrustedCertificate';

export default class Dashboard extends Component {
    constructor(props) {
        super(props);

        this.state ={
            specificPanel: ''
        };

        this.changePanel = this.changePanel.bind(this);
    }

    changePanel(specificPanel) {
        this.setState({
            specificPanel: 'verifyTrustedCertificate'
        });
    }

    render() {
        let panel = (<VerifyAccessPanel></VerifyAccessPanel>);

        if(this.state.specificPanel && this.state.specificPanel == "verifyTrustedCertificate") {
            panel = (<VerifyTrustedCertificate></VerifyTrustedCertificate>);
        }

        return (
            <div>
                <BasicMenu changePanel={this.changePanel}></BasicMenu>

                {panel}
            </div>
        );
    }
}

