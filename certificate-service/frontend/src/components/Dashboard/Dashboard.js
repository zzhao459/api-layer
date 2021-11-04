import { InputLabel } from '@mui/material';
import { Component } from 'react';
import BasicMenu from "../Menu/BasicMenu";
import VerifyAccessPanel from "../VerifyAccessPanel/VerifyAccessPanel";
import VerifyTrustedCertificatePanel from '../VerifyTrustedCertificatePanel/VerifyTrustedCertificatePanel';

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
            specificPanel: specificPanel
        });
    }

    render() {
        let panel = (<VerifyAccessPanel></VerifyAccessPanel>);

        if(this.state.specificPanel && this.state.specificPanel == "Verify the service is trusted") {
            panel = (<VerifyTrustedCertificatePanel></VerifyTrustedCertificatePanel>);
        }
        else if (this.state.specificPanel && this.state.specificPanel == "Verify access to a service") {
            panel = (<VerifyAccessPanel></VerifyAccessPanel>);
        }

        return (
            <div>
                <BasicMenu changePanel={this.changePanel}></BasicMenu>

                {panel}
            </div>
        );
    }
}

